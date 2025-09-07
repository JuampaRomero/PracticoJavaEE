@echo off
echo ========================================
echo  Cliente Remoto - Gestor de Identidades
echo ========================================
echo.

REM Construir el classpath con todas las dependencias
set CP=target\classes

REM Agregar todas las dependencias de Maven al classpath
for /r "%USERPROFILE%\.m2\repository" %%a in (*.jar) do (
    set CP=!CP!;%%a
)

REM Ejecutar el cliente remoto
java -cp "%CP%" com.ejercicio1.cliente.ClienteRemoto

pause
