$ErrorActionPreference = 'Stop'

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$SourceDir = Join-Path $Root 'PROJECT APLIKASI JAVA'
$BuildDir = Join-Path $Root 'build-exe'
$InputDir = Join-Path $BuildDir 'input'
$OutputDir = Join-Path $BuildDir 'output'
$ClassesDir = Join-Path $BuildDir 'classes'
$JarPath = Join-Path $InputDir 'AplikasiKeuangan.jar'
$ConnectorPath = Join-Path $SourceDir 'lib\mysql-connector-j-9.6.0.jar'
$JdkRoot = 'C:\Program Files\Java\jdk-25.0.2'
$Javac = Join-Path $JdkRoot 'bin\javac.exe'
$Jar = Join-Path $JdkRoot 'bin\jar.exe'
$Jpackage = Join-Path $JdkRoot 'bin\jpackage.exe'

if (-not (Test-Path $Javac)) {
    throw "javac tidak ditemukan di $Javac"
}

if (-not (Test-Path $Jar)) {
    throw "jar tidak ditemukan di $Jar"
}

if (-not (Test-Path $Jpackage)) {
    throw "jpackage tidak ditemukan di $Jpackage"
}

if (-not (Test-Path $ConnectorPath)) {
    throw "Connector MySQL tidak ditemukan: $ConnectorPath"
}

Remove-Item -Recurse -Force $BuildDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $InputDir | Out-Null
New-Item -ItemType Directory -Force $OutputDir | Out-Null
New-Item -ItemType Directory -Force $ClassesDir | Out-Null

Set-Location $SourceDir
& $Javac *.java

Copy-Item $ConnectorPath $InputDir

Get-ChildItem -Path $SourceDir -Filter *.class -File | Remove-Item -Force

# Compile ulang langsung ke classes folder agar jar hanya berisi bytecode aplikasi
Set-Location $SourceDir
& $Javac -d $ClassesDir *.java

Set-Location $Root
Copy-Item -Path (Join-Path $ClassesDir '*.class') -Destination $InputDir -Force

# Buat jar aplikasi dari kelas yang telah dikompilasi
$jarArgs = @('cf', $JarPath, '-C', $ClassesDir, '.')
& $Jar @jarArgs

& $Jpackage `
    --type app-image `
    --input $InputDir `
    --main-jar (Split-Path $JarPath -Leaf) `
    --main-class LoginFrame `
    --name AplikasiKeuangan `
    --vendor "Aplikasi Keuangan" `
    --app-version 1.0 `
    --dest $OutputDir `
    --java-options "-Dfile.encoding=UTF-8"

if ($LASTEXITCODE -ne 0) {
    throw "jpackage gagal dengan exit code $LASTEXITCODE"
}

$AppImageDir = Join-Path $OutputDir 'AplikasiKeuangan'
$AppDir = Join-Path $AppImageDir 'app'
$CfgContent = "# File konfigurasi launcher Java. Biarkan kosong jika tidak ada opsi khusus.\n"

New-Item -ItemType File -Path (Join-Path $AppImageDir 'AplikasiKeuangan.cfg') -Force | Out-Null
Set-Content -Path (Join-Path $AppImageDir 'AplikasiKeuangan.cfg') -Value $CfgContent

if (Test-Path $AppDir) {
    New-Item -ItemType File -Path (Join-Path $AppDir 'AplikasiKeuangan.cfg') -Force | Out-Null
    Set-Content -Path (Join-Path $AppDir 'AplikasiKeuangan.cfg') -Value $CfgContent
}

Write-Host "Build selesai. Launcher EXE tersedia di: $OutputDir\AplikasiKeuangan\AplikasiKeuangan.exe"
