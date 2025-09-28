# Script para compilar y desplegar GestorIdentidades en WildFly
# Autor: Sistema de despliegue automatizado
# Uso: .\deploy-wildfly.ps1

# Colores para output
$host.PrivateData.ErrorForegroundColor = 'Red'
$host.PrivateData.WarningForegroundColor = 'Yellow'

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   DESPLIEGUE EN WILDFLY - WINDOWS" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Variables de configuración
$projectDir = Get-Location
$warName = "GestorIdentidades-1.0-SNAPSHOT.war"
$wildflyPath = "C:\Program Files\WildFly"
$deploymentDir = "$wildflyPath\standalone\deployments"

# Función para verificar si un comando existe
function Test-CommandExists {
    param($command)
    $null = Get-Command $command -ErrorAction SilentlyContinue
    return $?
}

# 1. Verificar prerequisitos
Write-Host "[1/7] Verificando prerequisitos..." -ForegroundColor Yellow

# Verificar Maven
if (-not (Test-CommandExists "mvn")) {
    Write-Host "ERROR: Maven no está instalado o no está en el PATH" -ForegroundColor Red
    Write-Host "Instale Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Cyan
    exit 1
}
Write-Host "  [OK] Maven encontrado" -ForegroundColor Green

# Verificar Java
if (-not (Test-CommandExists "java")) {
    Write-Host "ERROR: Java no está instalado o no está en el PATH" -ForegroundColor Red
    exit 1
}
$javaVersion = java -version 2>&1 | Select-String "version"
Write-Host "  [OK] Java encontrado: $javaVersion" -ForegroundColor Green

# Verificar WildFly
if (-not (Test-Path $wildflyPath)) {
    Write-Host "ERROR: WildFly no encontrado en: $wildflyPath" -ForegroundColor Red
    Write-Host "Por favor, actualice la variable `$wildflyPath en este script" -ForegroundColor Yellow
    exit 1
}
Write-Host "  [OK] WildFly encontrado en: $wildflyPath" -ForegroundColor Green

# 2. Limpiar compilaciones anteriores
Write-Host "`n[2/7] Limpiando compilaciones anteriores..." -ForegroundColor Yellow
mvn clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al limpiar el proyecto" -ForegroundColor Red
    exit 1
}
Write-Host "  [OK] Proyecto limpiado" -ForegroundColor Green

# 3. Compilar el proyecto
Write-Host "`n[3/7] Compilando el proyecto..." -ForegroundColor Yellow
mvn package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo al compilar el proyecto" -ForegroundColor Red
    Write-Host "Revise los errores de compilación arriba" -ForegroundColor Yellow
    exit 1
}

# Verificar que el WAR se generó
$warPath = "$projectDir\target\$warName"
if (-not (Test-Path $warPath)) {
    Write-Host "ERROR: No se encontró el archivo WAR en: $warPath" -ForegroundColor Red
    exit 1
}
$warSize = (Get-Item $warPath).Length / 1MB
$warSizeFormatted = "{0:N2}" -f $warSize
Write-Host "  [OK] WAR generado exitosamente ($warSizeFormatted MB)" -ForegroundColor Green

# 4. Verificar si WildFly está ejecutándose
Write-Host "`n[4/7] Verificando estado de WildFly..." -ForegroundColor Yellow
$wildfly = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
    $_.CommandLine -like "*wildfly*" -or $_.CommandLine -like "*jboss*"
}

if (-not $wildfly) {
    Write-Host "  ! WildFly no está ejecutándose" -ForegroundColor Yellow
    Write-Host "  Iniciando WildFly..." -ForegroundColor Cyan
    
    $standaloneScript = "$wildflyPath\bin\standalone.bat"
    if (Test-Path $standaloneScript) {
        Start-Process -FilePath $standaloneScript -WindowStyle Normal
        Write-Host "  Esperando 20 segundos para que WildFly inicie..." -ForegroundColor Cyan
        Start-Sleep -Seconds 20
    } else {
        Write-Host "ERROR: No se encontró standalone.bat en: $standaloneScript" -ForegroundColor Red
        exit 1
    }
} else {
    $pid = $wildfly.Id
    Write-Host "  [OK] WildFly está ejecutándose (PID: $pid)" -ForegroundColor Green
}

# 5. Limpiar despliegues anteriores
Write-Host "`n[5/7] Limpiando despliegues anteriores..." -ForegroundColor Yellow

# Eliminar archivos de despliegue antiguos
$filesToRemove = @(
    "$deploymentDir\$warName",
    "$deploymentDir\$warName.deployed",
    "$deploymentDir\$warName.failed",
    "$deploymentDir\$warName.isdeploying",
    "$deploymentDir\$warName.pending",
    "$deploymentDir\$warName.undeployed"
)

foreach ($file in $filesToRemove) {
    if (Test-Path $file) {
        Remove-Item $file -Force
        Write-Host "  - Eliminado: $(Split-Path $file -Leaf)" -ForegroundColor Gray
    }
}

# Esperar un momento para que WildFly procese la eliminación
Start-Sleep -Seconds 3

# 6. Copiar el nuevo WAR
Write-Host "`n[6/7] Desplegando nueva versión..." -ForegroundColor Yellow
try {
    Copy-Item $warPath -Destination $deploymentDir -Force
    Write-Host "  [OK] WAR copiado a: $deploymentDir" -ForegroundColor Green
} catch {
    Write-Host "ERROR: No se pudo copiar el WAR: $_" -ForegroundColor Red
    exit 1
}

# 7. Verificar el despliegue
Write-Host "`n[7/7] Verificando despliegue..." -ForegroundColor Yellow
Write-Host "  Esperando respuesta de WildFly..." -ForegroundColor Cyan

$maxAttempts = 30
$attempt = 0
$deployed = $false

while ($attempt -lt $maxAttempts -and -not $deployed) {
    Start-Sleep -Seconds 2
    $attempt++
    
    # Verificar archivos de estado
    if (Test-Path "$deploymentDir\$warName.deployed") {
        $deployed = $true
        Write-Host "  [OK] Aplicación desplegada exitosamente!" -ForegroundColor Green
    }
    elseif (Test-Path "$deploymentDir\$warName.failed") {
        Write-Host "  [ERROR] El despliegue falló!" -ForegroundColor Red
        Write-Host "  Revise los logs en: $wildflyPath\standalone\log\server.log" -ForegroundColor Yellow
        exit 1
    }
    elseif (Test-Path "$deploymentDir\$warName.isdeploying") {
        Write-Host "  ... Desplegando (intento $attempt/$maxAttempts)" -ForegroundColor Gray
    }
}

if (-not $deployed) {
    Write-Host "  ! TIMEOUT: El despliegue tardó demasiado" -ForegroundColor Yellow
    Write-Host "  Revise el estado manualmente" -ForegroundColor Yellow
}

# Mostrar URLs de acceso
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "         DESPLIEGUE COMPLETADO" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nAcceda a la aplicación en:" -ForegroundColor Cyan
Write-Host "  http://localhost:8080/GestorIdentidades-1.0-SNAPSHOT/" -ForegroundColor White
Write-Host "  http://localhost:8080/GestorIdentidades-1.0-SNAPSHOT/index.xhtml" -ForegroundColor White
Write-Host "`nPara ver los logs de WildFly:" -ForegroundColor Cyan
Write-Host "  Get-Content '$wildflyPath\standalone\log\server.log' -Tail 50 -Wait" -ForegroundColor Gray
Write-Host "`n"