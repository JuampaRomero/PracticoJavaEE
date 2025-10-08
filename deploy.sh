#!/bin/zsh

# Ir al directorio del proyecto (opcional, si lo corres desde fuera)
cd ~/Developer/practico_1

# Ejecutar Maven build
echo "Ejecutando: mvn clean package"
mvn clean package

# Verificar si el build fue exitoso
if [[ $? -ne 0 ]]; then
  echo "❌ Error en la compilación con Maven."
  exit 1
fi

# Copiar el archivo .war al directorio de despliegue de WildFly
echo "Copiando el archivo .war al directorio de WildFly..."
cp ./target/*.war ~/Documents/java_ee/wildfly-37.0.0.Final/standalone/deployments/

# Verificar si la copia fue exitosa
if [[ $? -eq 0 ]]; then
  echo "✅ Deploy completado correctamente en WildFly."
else
  echo "❌ Error al copiar el archivo .war."
  exit 1
fi

echo "🚀 Iniciando WildFly..."
cd ~/Documents/java_ee/wildfly-37.0.0.Final/bin/

# Se ejecuta en foreground (bloquea la terminal)
./standalone.sh
