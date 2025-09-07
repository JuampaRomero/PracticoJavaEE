@echo off
echo ========================================
echo  Compilando y ejecutando Cliente Remoto
echo ========================================
echo.

REM Compilar el proyecto primero
echo Compilando proyecto...
call mvnw.cmd clean compile

REM Crear directorio para el cliente standalone
if not exist "client-standalone" mkdir client-standalone

REM Copiar las clases compiladas
echo Copiando clases...
xcopy /E /Y target\classes\* client-standalone\

REM Copiar todas las dependencias necesarias
echo Copiando dependencias...
call mvnw.cmd dependency:copy-dependencies -DoutputDirectory=client-standalone\lib

REM Construir el classpath
set CP=client-standalone
set CP=%CP%;client-standalone\lib\*

REM Ejecutar el cliente
echo.
echo ========================================
echo  Ejecutando Cliente Remoto
echo ========================================
echo.
java -cp "%CP%" com.ejercicio1.cliente.ClienteRemoto

pause
