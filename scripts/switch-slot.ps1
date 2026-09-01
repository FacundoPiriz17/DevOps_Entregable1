[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateSet("blue", "green")]
    [string]$Target
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

function Get-ServiceSlot {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Service
    )

    $slot = & kubectl get service $Service -o "jsonpath={.spec.selector.slot}"

    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo obtener el slot del Service $Service."
    }

    return $slot.Trim()
}

if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    throw "No se encontro kubectl en PATH."
}

$backendDeployment = "playhub-backend-$Target"
$frontendDeployment = "playhub-frontend-$Target"

$currentBackendSlot = Get-ServiceSlot -Service "playhub-backend"
$currentFrontendSlot = Get-ServiceSlot -Service "playhub-frontend"

if ($currentBackendSlot -ne $currentFrontendSlot) {
    throw "Estado inconsistente: frontend=$currentFrontendSlot, backend=$currentBackendSlot."
}

$currentSlot = $currentBackendSlot

if ($currentSlot -eq $Target) {
    Write-Host "PlayHub ya utiliza el slot '$Target'."
    exit 0
}

Write-Host "Comprobando que Backend $Target este disponible..."
Invoke-Kubectl -Arguments @("rollout", "status", "deployment/$backendDeployment", "--timeout=180s")

Write-Host "Comprobando que Frontend $Target este disponible..."
Invoke-Kubectl -Arguments @("rollout", "status", "deployment/$frontendDeployment", "--timeout=180s")

$targetPatch = '{"spec":{"selector":{"slot":"' + $Target + '"}}}'
$rollbackPatch = '{"spec":{"selector":{"slot":"' + $currentSlot + '"}}}'
$backendChanged = $false

try {
    Write-Host "Cambiando el backend de $currentSlot a $Target..."
    Invoke-Kubectl -Arguments @("patch", "service", "playhub-backend", "--type=merge", "--patch=$targetPatch")
    $backendChanged = $true

    Write-Host "Cambiando el frontend de $currentSlot a $Target..."
    Invoke-Kubectl -Arguments @("patch", "service", "playhub-frontend", "--type=merge", "--patch=$targetPatch")
}
catch {
    if ($backendChanged) {
        Write-Warning "Fallo el cambio del frontend. Restaurando el backend a $currentSlot..."
        Invoke-Kubectl -Arguments @("patch", "service", "playhub-backend", "--type=merge", "--patch=$rollbackPatch")
    }

    throw
}

Write-Host ""
Write-Host "PlayHub ahora utiliza el slot '$Target'."

Invoke-Kubectl -Arguments @(
    "get",
    "service",
    "playhub-backend",
    "playhub-frontend",
    "-o=custom-columns=SERVICE:.metadata.name,SLOT:.spec.selector.slot"
)

Write-Warning "Si utilizas port-forward, detenlo y vuelvelo a iniciar para conectarte a los Pods del nuevo slot."
