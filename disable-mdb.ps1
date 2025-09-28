# Script para deshabilitar temporalmente el MDB
$mdbPath = "src\main\java\com\ejercicio1\mdb\TrabajadorSaludMDB.java"
$backupPath = "src\main\java\com\ejercicio1\mdb\TrabajadorSaludMDB.java.backup"

# Crear backup
Copy-Item $mdbPath $backupPath

# Comentar la anotación @MessageDriven
$content = Get-Content $mdbPath -Raw
$content = $content -replace '@MessageDriven\(', '// @MessageDriven('
Set-Content $mdbPath $content

Write-Host "MDB deshabilitado temporalmente. Backup creado en $backupPath"