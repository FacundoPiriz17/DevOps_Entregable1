[CmdletBinding()]
param(
    [switch]$DeployGreen
)

$ErrorActionPreference = "Stop"

function Invoke-Kubectl {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & kubectl @Arguments

    if ($LASTEXITCODE -ne 0) {
        throw "Fallo kubectl $($Arguments -join ' ')"
    }
}

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    throw "No se encontro kubectl en PATH."
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$migrationDirectory = Join-Path $projectRoot "backend/src/main/resources/db/migration"

if (-not (Test-Path -Path $migrationDirectory -PathType Container)) {
    throw "No se encontro el directorio de migraciones: $migrationDirectory"
}

Push-Location $projectRoot

try {
    Write-Host "Aplicando configuracion y PostgreSQL..."

    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/configmap.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/secret.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/postgres-pvc.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/postgres-service.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/postgres-deployment.yaml")
    Invoke-Kubectl -Arguments @("rollout", "status", "deployment/playhub-postgres", "--timeout=180s")

    Write-Host "Cargando las migraciones SQL en Kubernetes..."

    $migrationManifest = & kubectl create configmap playhub-migration-sql `
        --from-file="backend/src/main/resources/db/migration" `
        --dry-run=client `
        -o yaml

    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo generar el ConfigMap con las migraciones."
    }

    $migrationManifest | & kubectl apply -f -

    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo aplicar el ConfigMap con las migraciones."
    }

    Write-Host "Ejecutando el Job de Flyway..."

    Invoke-Kubectl -Arguments @("delete", "job", "playhub-migrations", "--ignore-not-found=true")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/migration-job.yaml")
    Invoke-Kubectl -Arguments @("wait", "--for=condition=complete", "job/playhub-migrations", "--timeout=300s")
    Invoke-Kubectl -Arguments @("logs", "job/playhub-migrations")

    Write-Host "Desplegando Services y el slot Blue..."

    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/backend-service.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/frontend-service.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/backend-blue-deployment.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/frontend-blue-deployment.yaml")
    Invoke-Kubectl -Arguments @("rollout", "status", "deployment/playhub-backend-blue", "--timeout=180s")
    Invoke-Kubectl -Arguments @("rollout", "status", "deployment/playhub-frontend-blue", "--timeout=180s")

    if ($DeployGreen) {
        Write-Host "Desplegando tambien el slot Green..."

        Invoke-Kubectl -Arguments @("apply", "-f", "k8s/backend-green-deployment.yaml")
        Invoke-Kubectl -Arguments @("apply", "-f", "k8s/frontend-green-deployment.yaml")
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/playhub-backend-green", "--timeout=180s")
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/playhub-frontend-green", "--timeout=180s")
    }

    Write-Host ""
    Write-Host "Despliegue terminado. Slot activo: Blue"

    Invoke-Kubectl -Arguments @("get", "pods")
    Invoke-Kubectl -Arguments @("get", "jobs")
    Invoke-Kubectl -Arguments @("get", "pvc")
    Invoke-Kubectl -Arguments @("get", "services")
}
finally {
    Pop-Location
}
