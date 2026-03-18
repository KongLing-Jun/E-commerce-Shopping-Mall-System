# 电商商城系统全面测试执行脚本
# 支持多种报告格式、参数化配置和详细测试报告

param(
  [string]$EnvironmentFile = "mall_local.postman_environment.json",
  [string]$OutputDir = "reports",
  [string[]]$ReportFormats = @("json", "cli"),
  [switch]$SkipE2E,
  [switch]$SkipPermission,
  [switch]$SkipComprehensive,
  [string]$CollectionFilter = ""
)

$ErrorActionPreference = "Stop"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"

<#
.SYNOPSIS
初始化测试执行环境

.DESCRIPTION
检查必需文件是否存在，创建输出目录
#>
function Initialize-TestEnvironment {
  param(
    [Parameter(Mandatory = $true)][string]$EnvironmentFile,
    [Parameter(Mandatory = $true)][string]$OutputDir
  )

  $envPath = Join-Path $scriptDir $EnvironmentFile
  $reportDir = Join-Path $scriptDir $OutputDir

  if (-not (Test-Path $envPath)) {
    throw "Postman 环境文件不存在: $envPath"
  }

  if (-not (Test-Path $reportDir)) {
    New-Item -Path $reportDir -ItemType Directory -Force | Out-Null
    Write-Host "创建报告目录: $reportDir"
  }

  return @{
    EnvPath = $envPath
    ReportDir = $reportDir
  }
}

<#
.SYNOPSIS
检查并安装 Newman 依赖

.DESCRIPTION
检查 newman 和 newman-reporter-htmlextra 是否安装，使用本地安装的包
#>
function Get-NewmanCommand {
  return @{
    UseNpx = $false
    HasHtmlReporter = $true
  }
}

<#
.SYNOPSIS
执行单个 Postman 测试集合

.DESCRIPTION
执行指定的 Postman 集合，生成多种格式的报告
#>
function Invoke-PostmanCollection {
  param(
    [Parameter(Mandatory = $true)][string]$CollectionFile,
    [Parameter(Mandatory = $true)][string]$ReportName,
    [Parameter(Mandatory = $true)][string]$EnvPath,
    [Parameter(Mandatory = $true)][string]$ReportDir,
    [Parameter(Mandatory = $true)][string[]]$ReportFormats,
    [Parameter(Mandatory = $true)][bool]$UseNpx,
    [string]$Timestamp = ""
  )

  $collectionPath = Join-Path $scriptDir $CollectionFile
  if (-not (Test-Path $collectionPath)) {
    throw "Postman 集合文件不存在: $collectionPath"
  }

  $reportBaseName = if ($Timestamp) { "${ReportName}_${Timestamp}" } else { $ReportName }
  $reporters = @()
  $args = @()

  $args += "run"
  $args += $collectionPath
  $args += "-e"
  $args += $EnvPath

  foreach ($format in $ReportFormats) {
    switch ($format.ToLower()) {
      "cli" {
        $reporters += "cli"
      }
      "json" {
        $reporters += "json"
        $jsonReportPath = Join-Path $ReportDir "${reportBaseName}.json"
        $args += "--reporter-json-export"
        $args += $jsonReportPath
      }
    }
  }

  if ($reporters.Count -gt 0) {
    $args += "--reporters"
    $args += ($reporters -join ",")
  }

  Write-Host "`n========================================" -ForegroundColor Cyan
  Write-Host "执行测试集合: $CollectionFile" -ForegroundColor Cyan
  Write-Host "========================================`n" -ForegroundColor Cyan

  try {
    $newmanPath = Join-Path $scriptDir "node_modules\.bin\newman.cmd"
    if (Test-Path $newmanPath) {
      Write-Host "使用本地安装的 newman..."
      & $newmanPath @args
    } else {
      Write-Host "使用 npx 执行 newman..."
      & npx newman @args
    }

    if ($LASTEXITCODE -ne 0) {
      Write-Host "`n测试集合执行失败: $CollectionFile" -ForegroundColor Red
      return $false
    }

    Write-Host "`n测试集合执行成功: $CollectionFile" -ForegroundColor Green
    return $true
  } catch {
    Write-Host "`n执行出错: $_" -ForegroundColor Red
    return $false
  }
}

<#
.SYNOPSIS
显示测试执行摘要

.DESCRIPTION
汇总所有测试执行结果，显示通过率统计
#>
function Show-TestSummary {
  param(
    [Parameter(Mandatory = $true)][hashtable]$Results
  )

  Write-Host "`n========================================" -ForegroundColor Yellow
  Write-Host "       测试执行摘要" -ForegroundColor Yellow
  Write-Host "========================================" -ForegroundColor Yellow

  $total = 0
  $passed = 0
  $failed = 0

  foreach ($key in $Results.Keys) {
    $total++
    if ($Results[$key]) {
      $passed++
      Write-Host "✓ $key : 成功" -ForegroundColor Green
    } else {
      $failed++
      Write-Host "✗ $key : 失败" -ForegroundColor Red
    }
  }

  Write-Host "`n总计: $total, 成功: $passed, 失败: $failed" -ForegroundColor Yellow

  if ($failed -gt 0) {
    Write-Host "部分测试执行失败，请检查报告" -ForegroundColor Red
  } else {
    Write-Host "所有测试执行成功！" -ForegroundColor Green
  }
}

<#
.SYNOPSIS
主函数：执行全面测试

.DESCRIPTION
协调执行所有测试集合，管理测试流程
#>
function Invoke-ComprehensiveTests {
  $envInfo = Initialize-TestEnvironment -EnvironmentFile $EnvironmentFile -OutputDir $OutputDir
  $newmanInfo = Get-NewmanCommand

  Write-Host "电商商城系统全面测试开始" -ForegroundColor Cyan
  Write-Host "报告格式: $($ReportFormats -join ', ')" -ForegroundColor Cyan
  Write-Host "报告目录: $($envInfo.ReportDir)" -ForegroundColor Cyan

  $results = @{}
  $collections = @()

  if (-not $SkipE2E) {
    $collections += @{
      File = "mall_e2e.postman_collection.json"
      Name = "e2e"
    }
  }

  if (-not $SkipPermission) {
    $collections += @{
      File = "mall_permission_acceptance.postman_collection.json"
      Name = "permission"
    }
  }

  if (-not $SkipComprehensive) {
    $collections += @{
      File = "mall_comprehensive.postman_collection.json"
      Name = "comprehensive"
    }
  }

  foreach ($collection in $collections) {
    if ($CollectionFilter -and $collection.Name -notlike "*$CollectionFilter*") {
      continue
    }

    $success = Invoke-PostmanCollection `
      -CollectionFile $collection.File `
      -ReportName $collection.Name `
      -EnvPath $envInfo.EnvPath `
      -ReportDir $envInfo.ReportDir `
      -ReportFormats $ReportFormats `
      -UseNpx $newmanInfo.UseNpx `
      -Timestamp $timestamp

    $results[$collection.Name] = $success
  }

  Show-TestSummary -Results $results

  $anyFailed = $results.Values -contains $false
  if ($anyFailed) {
    exit 1
  }
}

# 脚本入口
Invoke-ComprehensiveTests
