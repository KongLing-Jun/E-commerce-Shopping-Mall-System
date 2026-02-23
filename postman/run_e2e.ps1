param(
  [string]$EnvironmentFile = "mall_local.postman_environment.json",
  [string]$OutputDir = "reports",
  [switch]$SkipPermission
)

$ErrorActionPreference = "Stop"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$envPath = Join-Path $scriptDir $EnvironmentFile
$reportDir = Join-Path $scriptDir $OutputDir

if (-not (Test-Path $envPath)) {
  throw "Postman environment file not found: $envPath"
}

New-Item -Path $reportDir -ItemType Directory -Force | Out-Null

function Invoke-Collection {
  param(
    [Parameter(Mandatory = $true)][string]$CollectionFile,
    [Parameter(Mandatory = $true)][string]$ReportName
  )

  $collectionPath = Join-Path $scriptDir $CollectionFile
  if (-not (Test-Path $collectionPath)) {
    throw "Postman collection not found: $collectionPath"
  }

  $reportPath = Join-Path $reportDir "$ReportName.json"
  $args = @(
    "run", $collectionPath,
    "-e", $envPath,
    "--reporters", "cli,json",
    "--reporter-json-export", $reportPath
  )

  $newman = Get-Command newman -ErrorAction SilentlyContinue
  if ($newman) {
    & $newman.Path @args
  } else {
    & npx --yes newman @args
  }

  if ($LASTEXITCODE -ne 0) {
    throw "Collection failed: $CollectionFile"
  }
}

if (-not $SkipPermission) {
  Invoke-Collection -CollectionFile "mall_permission_acceptance.postman_collection.json" -ReportName "permission"
}

Invoke-Collection -CollectionFile "mall_e2e.postman_collection.json" -ReportName "e2e"

Write-Host "E2E verification finished. Reports: $reportDir"
