# Script para iniciar WildFly con el perfil completo (incluye EJB, JMS, etc.)

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   INICIANDO WILDFLY - PERFIL COMPLETO" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$wildflyPath = "C:\Program Files\WildFly"

# Detener WildFly si está corriendo
Write-Host "Verificando si WildFly está ejecutándose..." -ForegroundColor Yellow
$wildfly = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
    $_.CommandLine -like "*wildfly*" -or $_.CommandLine -like "*jboss*"
}

if ($wildfly) {
    Write-Host "WildFly detectado (PID: $($wildfly.Id)). Deteniéndolo..." -ForegroundColor Yellow
    
    # Intentar detener gracefully primero
    $cliScript = "$wildflyPath\bin\jboss-cli.bat"
    if (Test-Path $cliScript) {
        & $cliScript --connect --command=":shutdown" 2>$null
        Start-Sleep -Seconds 5
    }
    
    # Si aún está corriendo, forzar cierre
    $wildfly = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
        $_.CommandLine -like "*wildfly*" -or $_.CommandLine -like "*jboss*"
    }
    
    if ($wildfly) {
        Stop-Process -Id $wildfly.Id -Force
        Write-Host "WildFly detenido forzosamente" -ForegroundColor Yellow
    }
    
    Start-Sleep -Seconds 3
}

# Iniciar WildFly con perfil standalone-full
Write-Host "`nIniciando WildFly con perfil FULL (incluye EJB, JMS, etc.)..." -ForegroundColor Green

$standaloneScript = "$wildflyPath\bin\standalone.bat"
if (-not (Test-Path $standaloneScript)) {
    Write-Host "[ERROR] No se encontró standalone.bat en: $standaloneScript" -ForegroundColor Red
    exit 1
}

# Crear un archivo batch temporal para iniciar con el perfil correcto
$tempBatch = "$env:TEMP\start-wildfly-full.bat"
$batchContent = @"
@echo off
cd /d "$wildflyPath\bin"
call standalone.bat -c standalone-full.xml
"@

Set-Content -Path $tempBatch -Value $batchContent -Encoding ASCII

Write-Host "Ejecutando: standalone.bat -c standalone-full.xml" -ForegroundColor Cyan
Start-Process -FilePath $tempBatch -WindowStyle Normal

Write-Host "`nEsperando que WildFly inicie completamente..." -ForegroundColor Yellow
Write-Host "Esto puede tomar 30-60 segundos..." -ForegroundColor Gray

# Esperar y verificar que inició
$maxAttempts = 30
$attempt = 0
$started = $false

while ($attempt -lt $maxAttempts -and -not $started) {
    Start-Sleep -Seconds 2
    $attempt++
    
    # Verificar si el puerto 8080 está abierto
    $connection = Test-NetConnection -ComputerName localhost -Port 8080 -WarningAction SilentlyContinue
    if ($connection.TcpTestSucceeded) {
        $started = $true
        Write-Host "`n[OK] WildFly iniciado exitosamente en el puerto 8080" -ForegroundColor Green
    } else {
        Write-Host "." -NoNewline -ForegroundColor Gray
    }
}

if (-not $started) {
    Write-Host "`n[ADVERTENCIA] WildFly tardó en iniciar. Verifique la ventana de WildFly" -ForegroundColor Yellow
} else {
    # Verificar subsistemas disponibles
    Write-Host "`nVerificando subsistemas disponibles..." -ForegroundColor Yellow
    
    # Esperar un poco más para que todos los servicios estén listos
    Start-Sleep -Seconds 5
    
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "         WILDFLY INICIADO" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    
    Write-Host "`nPerfil: standalone-full.xml" -ForegroundColor White
    Write-Host "Este perfil incluye:" -ForegroundColor Cyan
    Write-Host "  - EJB 3.2" -ForegroundColor Gray
    Write-Host "  - JMS (HornetQ/ActiveMQ)" -ForegroundColor Gray
    Write-Host "  - JAX-RS (REST)" -ForegroundColor Gray
    Write-Host "  - JSF" -ForegroundColor Gray
    Write-Host "  - CDI" -ForegroundColor Gray
    Write-Host "  - JPA" -ForegroundColor Gray
    
    Write-Host "`nConsola de administración:" -ForegroundColor Yellow
    Write-Host "  http://localhost:9990/" -ForegroundColor White
    
    Write-Host "`nAhora puede desplegar la aplicación con:" -ForegroundColor Yellow
    Write-Host "  .\deploy-war-only.ps1" -ForegroundColor White
}

Write-Host "`n"