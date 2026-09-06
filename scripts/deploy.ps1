[CmdletBinding()]
param(
    [switch]$DeployGreen,

    [ValidateSet("auto", "docker-desktop", "kind", "minikube", "k3d", "none")]
    [string]$ClusterType = "auto",

    [string]$ClusterName,

    [switch]$SkipBuild,

    [switch]$SkipImageLoad,

    [ValidatePattern("^https?://")]
    [string]$FrontendApiUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

function Assert-Command {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "No se encontro '$Name' en PATH."
    }
}

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

function Get-KubectlOutput {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & kubectl @Arguments

    if ($LASTEXITCODE -ne 0) {
        throw "Fallo kubectl $($Arguments -join ' ')"
    }

    return ($output -join "`n").Trim()
}

function Test-KubectlResourceExists {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Kind,

        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    $resource = & kubectl get $Kind $Name --ignore-not-found=true -o name 2>$null

    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo comprobar si existe $Kind/$Name."
    }

    return -not [string]::IsNullOrWhiteSpace(($resource -join "`n").Trim())
}

function Wait-KubernetesJob {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name,

        [int]$TimeoutSeconds = 300,

        [int]$PollIntervalSeconds = 2
    )

    $deadline = [DateTimeOffset]::UtcNow.AddSeconds($TimeoutSeconds)

    while ([DateTimeOffset]::UtcNow -lt $deadline) {
        $jobJson = & kubectl get job $Name -o json

        if ($LASTEXITCODE -ne 0) {
            throw "No se pudo consultar el estado de job/$Name."
        }

        $job = $jobJson | ConvertFrom-Json
        $conditions = @($job.status.conditions)
        $completed = $conditions |
            Where-Object { $_.type -eq "Complete" -and $_.status -eq "True" } |
            Select-Object -Last 1

        if ($null -ne $completed) {
            Write-Host "Job completado: $Name"
            return
        }

        $failed = $conditions |
            Where-Object { $_.type -eq "Failed" -and $_.status -eq "True" } |
            Select-Object -Last 1

        if ($null -ne $failed) {
            $reason = if ([string]::IsNullOrWhiteSpace($failed.reason)) {
                "sin motivo informado"
            }
            else {
                $failed.reason
            }

            throw "El Job job/$Name fallo: $reason."
        }

        Start-Sleep -Seconds $PollIntervalSeconds
    }

    throw "Tiempo agotado esperando que job/$Name termine (${TimeoutSeconds}s)."
}

function Show-JobDiagnostics {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    Write-Warning "Diagnostico de job/${Name}:"
    & kubectl get job $Name -o wide
    & kubectl describe job $Name
    & kubectl get pods -l "job-name=$Name" -o wide
    & kubectl logs -l "job-name=$Name" --all-containers=true --prefix=true --tail=-1
}

function Invoke-Docker {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    & docker @Arguments

    if ($LASTEXITCODE -ne 0) {
        throw "Fallo docker $($Arguments -join ' ')"
    }
}

function Resolve-ClusterType {
    param(
        [Parameter(Mandatory = $true)]
        [string]$RequestedType,

        [Parameter(Mandatory = $true)]
        [string]$Context
    )

    if ($RequestedType -ne "auto") {
        return $RequestedType
    }

    if ($Context -eq "docker-desktop") {
        return "docker-desktop"
    }

    if ($Context.StartsWith("kind-")) {
        return "kind"
    }

    if ($Context.StartsWith("k3d-")) {
        return "k3d"
    }

    if ($Context -eq "minikube") {
        return "minikube"
    }

    throw @"
No se pudo detectar el tipo de cluster a partir del contexto '$Context'.
Indicalo con -ClusterType docker-desktop|kind|minikube|k3d.
Si las imagenes ya fueron precargadas manualmente, usa -ClusterType none.
"@
}

function Resolve-ClusterName {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ResolvedType,

        [Parameter(Mandatory = $true)]
        [string]$Context,

        [AllowEmptyString()]
        [string]$RequestedName
    )

    if (-not [string]::IsNullOrWhiteSpace($RequestedName)) {
        return $RequestedName
    }

    switch ($ResolvedType) {
        "kind" {
            if ($Context.StartsWith("kind-")) {
                return $Context.Substring(5)
            }

            return "kind"
        }
        "k3d" {
            if ($Context.StartsWith("k3d-")) {
                return $Context.Substring(4)
            }

            throw "Debes indicar -ClusterName para un cluster k3d no detectable."
        }
        "minikube" {
            if ($Context -eq "minikube") {
                return "minikube"
            }

            return $Context
        }
        default {
            return ""
        }
    }
}

function Import-ClusterImages {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Images,

        [Parameter(Mandatory = $true)]
        [string]$ResolvedType,

        [AllowEmptyString()]
        [string]$ResolvedName
    )

    switch ($ResolvedType) {
        "docker-desktop" {
            Write-Host "Docker Desktop usara las imagenes construidas en su image store actual."
        }
        "kind" {
            Assert-Command -Name "kind"
            & kind load docker-image @Images --name $ResolvedName

            if ($LASTEXITCODE -ne 0) {
                throw "No se pudieron cargar las imagenes en kind '$ResolvedName'."
            }
        }
        "minikube" {
            Assert-Command -Name "minikube"

            foreach ($image in $Images) {
                & minikube -p $ResolvedName image load $image

                if ($LASTEXITCODE -ne 0) {
                    throw "No se pudo cargar '$image' en Minikube '$ResolvedName'."
                }
            }
        }
        "k3d" {
            Assert-Command -Name "k3d"
            & k3d image import @Images --cluster $ResolvedName

            if ($LASTEXITCODE -ne 0) {
                throw "No se pudieron cargar las imagenes en k3d '$ResolvedName'."
            }
        }
        "none" {
            Write-Warning "Se omitio la carga automatica. Las imagenes deben existir en todos los nodos."
        }
    }
}

function Test-ClusterImage {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Image,

        [Parameter(Mandatory = $true)]
        [int]$Index
    )

    $checkPod = "playhub-image-check-$Index-$PID"
    & kubectl delete pod $checkPod --ignore-not-found=true --wait=false 2>$null | Out-Null

    try {
        Invoke-Kubectl -Arguments @(
            "run",
            $checkPod,
            "--image=$Image",
            "--image-pull-policy=Never",
            "--labels=app=playhub-image-check",
            "--restart=Never",
            "--command",
            "--",
            "/bin/sh",
            "-c",
            "exit 0"
        )

        for ($attempt = 0; $attempt -lt 45; $attempt++) {
            $phase = Get-KubectlOutput -Arguments @(
                "get",
                "pod",
                $checkPod,
                "-o",
                "jsonpath={.status.phase}"
            )

            if ($phase -eq "Succeeded") {
                Write-Host "Imagen disponible en Kubernetes: $Image"
                return
            }

            $waitingReason = & kubectl @(
                "get",
                "pod",
                $checkPod,
                "-o",
                "jsonpath={.status.containerStatuses[0].state.waiting.reason}"
            ) 2>$null

            if ($LASTEXITCODE -ne 0) {
                $waitingReason = ""
            }
            else {
                $waitingReason = ($waitingReason -join "`n").Trim()
            }

            if ($waitingReason -in @("ErrImageNeverPull", "ImagePullBackOff", "InvalidImageName")) {
                Invoke-Kubectl -Arguments @("describe", "pod", $checkPod)
                throw "La imagen '$Image' no esta disponible en el runtime del nodo ($waitingReason)."
            }

            if ($phase -eq "Failed") {
                Invoke-Kubectl -Arguments @("describe", "pod", $checkPod)
                throw "La comprobacion de la imagen '$Image' termino en estado Failed."
            }

            Start-Sleep -Seconds 1
        }

        Invoke-Kubectl -Arguments @("describe", "pod", $checkPod)
        throw "Tiempo agotado comprobando la imagen '$Image'."
    }
    finally {
        & kubectl delete pod $checkPod --ignore-not-found=true --wait=false 2>$null | Out-Null
    }
}

function Show-DeploymentDiagnostics {
    Write-Warning "El despliegue no se completo. Estado actual del cluster:"
    & kubectl get pods -o wide
    & kubectl get events --sort-by=.lastTimestamp
}

Assert-Command -Name "kubectl"

$projectRoot = Split-Path -Parent $PSScriptRoot
$migrationDirectory = Join-Path $projectRoot "backend/src/main/resources/db/migration"

if (-not (Test-Path -LiteralPath $migrationDirectory -PathType Container)) {
    throw "No se encontro el directorio de migraciones: $migrationDirectory"
}

$currentContext = Get-KubectlOutput -Arguments @("config", "current-context")

if ([string]::IsNullOrWhiteSpace($currentContext)) {
    throw "kubectl no tiene un contexto activo."
}

$currentNamespace = Get-KubectlOutput -Arguments @(
    "config",
    "view",
    "--minify",
    "-o",
    "jsonpath={..namespace}"
)

if ([string]::IsNullOrWhiteSpace($currentNamespace)) {
    $currentNamespace = "default"
}

$resolvedClusterType = Resolve-ClusterType -RequestedType $ClusterType -Context $currentContext
$resolvedClusterName = Resolve-ClusterName `
    -ResolvedType $resolvedClusterType `
    -Context $currentContext `
    -RequestedName $ClusterName

Write-Host "Contexto Kubernetes: $currentContext"
Write-Host "Namespace: $currentNamespace"
Write-Host "Tipo de cluster: $resolvedClusterType"

if (-not [string]::IsNullOrWhiteSpace($resolvedClusterName)) {
    Write-Host "Nombre del cluster: $resolvedClusterName"
}

if ($resolvedClusterType -eq "minikube") {
    Assert-Command -Name "minikube"
    & minikube -p $resolvedClusterName status

    if ($LASTEXITCODE -ne 0) {
        throw "El perfil Minikube '$resolvedClusterName' no esta iniciado. Ejecuta: minikube start -p $resolvedClusterName"
    }
}

Invoke-Kubectl -Arguments @("cluster-info")

$images = @(
    "playhub-backend:v1.0",
    "playhub-frontend:v1.0"
)

if ($DeployGreen) {
    $images += @(
        "playhub-backend:v2.0",
        "playhub-frontend:v2.0"
    )
}

$manifestFiles = @(
    "k8s/configmap.yaml",
    "k8s/secret.yaml",
    "k8s/postgres-pvc.yaml",
    "k8s/postgres-service.yaml",
    "k8s/postgres-deployment.yaml",
    "k8s/migration-job.yaml",
    "k8s/backend-service.yaml",
    "k8s/frontend-service.yaml",
    "k8s/backend-blue-deployment.yaml",
    "k8s/frontend-blue-deployment.yaml"
)

if ($DeployGreen) {
    $manifestFiles += @(
        "k8s/backend-green-deployment.yaml",
        "k8s/frontend-green-deployment.yaml"
    )
}

Push-Location $projectRoot

try {
    Write-Host "Validando manifiestos contra el cluster..."

    foreach ($manifestFile in $manifestFiles) {
        Invoke-Kubectl -Arguments @("apply", "--dry-run=client", "--validate=strict", "-f", $manifestFile)
    }

    if (-not $SkipBuild) {
        Assert-Command -Name "docker"
        Invoke-Docker -Arguments @("info", "--format", "{{.ServerVersion}}")

        Write-Host "Construyendo imagenes Blue..."
        Invoke-Docker -Arguments @(
            "build",
            "--file",
            "backend/Dockerfile.prod",
            "--tag",
            "playhub-backend:v1.0",
            "backend"
        )
        Invoke-Docker -Arguments @(
            "build",
            "--build-arg",
            "NEXT_PUBLIC_API_URL=$FrontendApiUrl",
            "--file",
            "frontend/Dockerfile.prod",
            "--tag",
            "playhub-frontend:v1.0",
            "frontend"
        )

        if ($DeployGreen) {
            Write-Host "Construyendo imagenes Green..."
            Invoke-Docker -Arguments @(
                "build",
                "--file",
                "backend/Dockerfile.prod",
                "--tag",
                "playhub-backend:v2.0",
                "backend"
            )
            Invoke-Docker -Arguments @(
                "build",
                "--build-arg",
                "NEXT_PUBLIC_API_URL=$FrontendApiUrl",
                "--file",
                "frontend/Dockerfile.prod",
                "--tag",
                "playhub-frontend:v2.0",
                "frontend"
            )
        }
    }
    else {
        Write-Warning "Se omitio la construccion de imagenes."
    }

    if (-not $SkipImageLoad) {
        Import-ClusterImages `
            -Images $images `
            -ResolvedType $resolvedClusterType `
            -ResolvedName $resolvedClusterName
    }
    else {
        Write-Warning "Se omitio la carga de imagenes en el cluster."
    }

    Write-Host "Comprobando que Kubernetes puede iniciar las imagenes..."

    for ($index = 0; $index -lt $images.Count; $index++) {
        Test-ClusterImage -Image $images[$index] -Index ($index + 1)
    }

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
        -o json

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

    try {
        Wait-KubernetesJob -Name "playhub-migrations" -TimeoutSeconds 300
    }
    catch {
        Show-JobDiagnostics -Name "playhub-migrations"
        throw
    }

    Invoke-Kubectl -Arguments @("logs", "job/playhub-migrations")

    Write-Host "Desplegando Services y el slot Blue..."

    $backendBlueExisted = Test-KubectlResourceExists `
        -Kind "deployment" `
        -Name "playhub-backend-blue"
    $frontendBlueExisted = Test-KubectlResourceExists `
        -Kind "deployment" `
        -Name "playhub-frontend-blue"

    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/backend-service.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/frontend-service.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/backend-blue-deployment.yaml")
    Invoke-Kubectl -Arguments @("apply", "-f", "k8s/frontend-blue-deployment.yaml")

    if ($backendBlueExisted) {
        Invoke-Kubectl -Arguments @("rollout", "restart", "deployment/playhub-backend-blue")
    }

    if ($frontendBlueExisted) {
        Invoke-Kubectl -Arguments @("rollout", "restart", "deployment/playhub-frontend-blue")
    }

    try {
        Invoke-Kubectl -Arguments @(
            "rollout",
            "status",
            "deployment/playhub-backend-blue",
            "--timeout=300s"
        )
        Invoke-Kubectl -Arguments @(
            "rollout",
            "status",
            "deployment/playhub-frontend-blue",
            "--timeout=300s"
        )

        if ($DeployGreen) {
            Write-Host "Desplegando tambien el slot Green..."

            $backendGreenExisted = Test-KubectlResourceExists `
                -Kind "deployment" `
                -Name "playhub-backend-green"
            $frontendGreenExisted = Test-KubectlResourceExists `
                -Kind "deployment" `
                -Name "playhub-frontend-green"

            Invoke-Kubectl -Arguments @("apply", "-f", "k8s/backend-green-deployment.yaml")
            Invoke-Kubectl -Arguments @("apply", "-f", "k8s/frontend-green-deployment.yaml")

            if ($backendGreenExisted) {
                Invoke-Kubectl -Arguments @("rollout", "restart", "deployment/playhub-backend-green")
            }

            if ($frontendGreenExisted) {
                Invoke-Kubectl -Arguments @("rollout", "restart", "deployment/playhub-frontend-green")
            }

            Invoke-Kubectl -Arguments @(
                "rollout",
                "status",
                "deployment/playhub-backend-green",
                "--timeout=300s"
            )
            Invoke-Kubectl -Arguments @(
                "rollout",
                "status",
                "deployment/playhub-frontend-green",
                "--timeout=300s"
            )
        }
    }
    catch {
        Show-DeploymentDiagnostics
        throw
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
