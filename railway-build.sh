#!/bin/bash
set -e

echo "🚀 Starting Railway build process..."

# Configurar Maven para reintentos
export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m"

echo "📦 Downloading dependencies..."
# Intentar descargar dependencias con reintentos
for i in {1..3}; do
    echo "Attempt $i of 3..."
    if mvn dependency:go-offline -B -Dmaven.wagon.http.retryHandler.count=5; then
        echo "✅ Dependencies downloaded successfully"
        break
    else
        echo "⚠️  Attempt $i failed, retrying..."
        sleep 5
    fi
done

echo "🔨 Building WAR file..."
# Construir el proyecto
if mvn clean package -DskipTests -B; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed, trying offline mode..."
    mvn clean package -DskipTests -o -B
fi

echo "📋 Listing build artifacts..."
ls -la target/

echo "✅ Railway build complete!"
