#!/bin/bash

echo "🔍 Verificando deployment local para Railway..."

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

# Limpiar y construir el proyecto
echo "📦 Construyendo el WAR..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Error al construir el WAR"
    exit 1
fi

# Construir la imagen Docker
echo "🐳 Construyendo imagen Docker..."
docker build -t gestor-identidades-test .

if [ $? -ne 0 ]; then
    echo "❌ Error al construir la imagen Docker"
    exit 1
fi

# Ejecutar el contenedor
echo "🚀 Ejecutando contenedor de prueba..."
docker run -d --name gestor-test -p 8080:8080 -e PORT=8080 gestor-identidades-test

# Esperar a que la aplicación inicie
echo "⏳ Esperando que la aplicación inicie (30 segundos)..."
sleep 30

# Verificar que la aplicación responda
echo "🔍 Verificando que la aplicación responda..."
curl -f http://localhost:8080/ > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo "✅ La aplicación está funcionando correctamente!"
    echo "📋 Puedes acceder a: http://localhost:8080/"
    echo ""
    echo "🧹 Para limpiar, ejecuta:"
    echo "   docker stop gestor-test && docker rm gestor-test"
else
    echo "❌ La aplicación no responde. Revisa los logs con:"
    echo "   docker logs gestor-test"
    docker stop gestor-test && docker rm gestor-test
    exit 1
fi

echo ""
echo "✅ Verificación completa! Tu aplicación está lista para Railway."
