# Script simplificado para desplegar solo el WAR (sin compilar)
# Útil cuando ya tienes el WAR compilado

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   DESPLIEGUE RÁPIDO DE WAR" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Variables
$warName = "GestorIdentidades-1.0-SNAPSHOT.war"
$wildflyPath = "C:\Program Files\WildFly"
$deploymentDir = "$wildflyPath\standalone\deployments"
$warPath = ".\target\$warName"

# Verificar si existe el WAR
if (-not (Test-Path $warPath)) {
    Write-Host "[ERROR] No se encontró el archivo WAR en: $warPath" -ForegroundColor Red
    Write-Host "`nOpciones:" -ForegroundColor Yellow
    Write-Host "1. Compile el proyecto con: mvn clean package" -ForegroundColor Gray
    Write-Host "2. O descargue el WAR compilado y colóquelo en la carpeta target" -ForegroundColor Gray
    exit 1
}

$warSize = (Get-Item $warPath).Length / 1MB
$warSizeFormatted = "{0:N2}" -f $warSize
Write-Host "[OK] WAR encontrado: $warName ($warSizeFormatted MB)" -ForegroundColor Green

# Verificar WildFly
if (-not (Test-Path $wildflyPath)) {
    Write-Host "[ERROR] WildFly no encontrado en: $wildflyPath" -ForegroundColor Red
    
    # Buscar en otras ubicaciones comunes
    $alternativePaths = @(
        "C:\wildfly",
        "C:\servers\wildfly",
        "$env:USERPROFILE\wildfly"
    )
    
    foreach ($path in $alternativePaths) {
        if (Test-Path $path) {
            Write-Host "[INFO] WildFly encontrado en: $path" -ForegroundColor Yellow
            $response = Read-Host "¿Usar esta ubicación? (S/N)"
            if ($response -eq "S") {
                $wildflyPath = $path
                $deploymentDir = "$wildflyPath\standalone\deployments"
                break
            }
        }
    }
    
    if (-not (Test-Path $wildflyPath)) {
        exit 1
    }
}

Write-Host "[OK] WildFly encontrado en: $wildflyPath" -ForegroundColor Green

# Verificar si WildFly está corriendo
Write-Host "`nVerificando estado de WildFly..." -ForegroundColor Yellow
$wildfly = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
    $_.CommandLine -like "*wildfly*" -or $_.CommandLine -like "*jboss*"
}

if (-not $wildfly) {
    Write-Host "[AVISO] WildFly no está ejecutándose" -ForegroundColor Yellow
    $response = Read-Host "¿Desea iniciarlo ahora? (S/N)"
    
    if ($response -eq "S") {
        $standaloneScript = "$wildflyPath\bin\standalone.bat"
        if (Test-Path $standaloneScript) {
            Write-Host "Iniciando WildFly..." -ForegroundColor Cyan
            Start-Process -FilePath $standaloneScript -WindowStyle Minimized
            Write-Host "Esperando 20 segundos para que inicie..." -ForegroundColor Cyan
            Start-Sleep -Seconds 20
        }
    } else {
        Write-Host "`n[INFO] Inicie WildFly manualmente con:" -ForegroundColor Yellow
        Write-Host "  $wildflyPath\bin\standalone.bat" -ForegroundColor Gray
        exit 0
    }
} else {
    $pid = $wildfly.Id
    Write-Host "[OK] WildFly está ejecutándose (PID: $pid)" -ForegroundColor Green
}

# Limpiar despliegues anteriores
Write-Host "`nLimpiando despliegues anteriores..." -ForegroundColor Yellow

$filesToRemove = @(
    "$deploymentDir\$warName",
    "$deploymentDir\$warName.deployed",
    "$deploymentDir\$warName.failed",
    "$deploymentDir\$warName.isdeploying",
    "$deploymentDir\$warName.pending",
    "$deploymentDir\$warName.undeployed"
)

$removedCount = 0
foreach ($file in $filesToRemove) {
    if (Test-Path $file) {
        Remove-Item $file -Force -ErrorAction SilentlyContinue
        $removedCount++
    }
}

if ($removedCount -gt 0) {
    Write-Host "[OK] Se eliminaron $removedCount archivos antiguos" -ForegroundColor Green
    Start-Sleep -Seconds 2
}

# Copiar el WAR
Write-Host "`nCopiando WAR al directorio de despliegue..." -ForegroundColor Yellow
try {
    Copy-Item $warPath -Destination $deploymentDir -Force
    Write-Host "[OK] WAR copiado exitosamente" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] No se pudo copiar el WAR: $_" -ForegroundColor Red
    Write-Host "`nVerifique los permisos en: $deploymentDir" -ForegroundColor Yellow
    exit 1
}

# Crear archivo .dodeploy para forzar el despliegue
$dodeployFile = "$deploymentDir\$warName.dodeploy"
New-Item -Path $dodeployFile -ItemType File -Force | Out-Null
Write-Host "[OK] Archivo .dodeploy creado" -ForegroundColor Green

# Verificar el despliegue
Write-Host "`nVerificando despliegue..." -ForegroundColor Yellow
Write-Host "Esperando respuesta de WildFly..." -ForegroundColor Cyan

$maxAttempts = 30
$attempt = 0
$deployed = $false

while ($attempt -lt $maxAttempts -and -not $deployed) {
    Start-Sleep -Seconds 2
    $attempt++
    
    if (Test-Path "$deploymentDir\$warName.deployed") {
        $deployed = $true
        Write-Host "`n[OK] ¡Aplicación desplegada exitosamente!" -ForegroundColor Green
    }
    elseif (Test-Path "$deploymentDir\$warName.failed") {
        Write-Host "`n[ERROR] El despliegue falló" -ForegroundColor Red
        
        # Intentar leer el archivo .failed para más información
        if (Test-Path "$deploymentDir\$warName.failed") {
            $errorContent = Get-Content "$deploymentDir\$warName.failed" -ErrorAction SilentlyContinue
            if ($errorContent) {
                Write-Host "`nDetalles del error:" -ForegroundColor Yellow
                Write-Host $errorContent -ForegroundColor Gray
            }
        }
        
        Write-Host "`nRevise los logs para más detalles:" -ForegroundColor Yellow
        Write-Host "  $wildflyPath\standalone\log\server.log" -ForegroundColor Gray
        exit 1
    }
    else {
        Write-Host "." -NoNewline -ForegroundColor Gray
    }
}

if (-not $deployed) {
    Write-Host "`n[TIMEOUT] El despliegue tardó demasiado" -ForegroundColor Yellow
    Write-Host "Verifique manualmente el estado en:" -ForegroundColor Yellow
    Write-Host "  $deploymentDir" -ForegroundColor Gray
} else {
    # Éxito - mostrar URLs
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "        ¡DESPLIEGUE EXITOSO!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    
    Write-Host "`nAcceda a la aplicación en:" -ForegroundColor Cyan
    Write-Host "  http://localhost:8080/GestorIdentidades-1.0-SNAPSHOT/" -ForegroundColor White
    Write-Host "  http://localhost:8080/GestorIdentidades-1.0-SNAPSHOT/index.xhtml" -ForegroundColor White
    
    Write-Host "`nEndpoints disponibles:" -ForegroundColor Cyan
    Write-Host "  - /api/trabajadores (REST)" -ForegroundColor Gray
    Write-Host "  - /trabajadores/listar.xhtml" -ForegroundColor Gray
    Write-Host "  - /trabajadores/registrar.xhtml" -ForegroundColor Gray
    
    Write-Host "`nComandos útiles:" -ForegroundColor Yellow
    Write-Host "  Ver logs: Get-Content '$wildflyPath\standalone\log\server.log' -Tail 50 -Wait" -ForegroundColor Gray
    Write-Host "  Estado: Get-ChildItem '$deploymentDir' | Select-Object Name, LastWriteTime" -ForegroundColor Gray
}

Write-Host "`n"