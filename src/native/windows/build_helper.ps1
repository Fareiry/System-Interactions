$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Resolve-Path (Join-Path $scriptDir '..\..')
$outputDir = Join-Path $projectRoot 'main\resources\assets\system_interactions\native\windows'
$outputPath = Join-Path $outputDir 'MinecraftToastHelper.exe'
$sourcePath = Join-Path $scriptDir 'MinecraftToastHelper.cs'

New-Item -ItemType Directory -Path $outputDir -Force | Out-Null

Add-Type `
    -Path $sourcePath `
    -ReferencedAssemblies 'System.Windows.Forms','System.Drawing' `
    -OutputAssembly $outputPath `
    -OutputType WindowsApplication

Write-Host "Built $outputPath"
