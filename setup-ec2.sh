#!/bin/bash
# Script de configuración inicial para EC2
# Ejecutar una sola vez en la instancia EC2 para preparar el entorno

set -e

echo "🔧 Configurando instancia EC2 para GestorIdentidades..."

# Actualizar sistema
echo "📦 Actualizando paquetes del sistema..."
sudo apt-get update
sudo apt-get upgrade -y

# Instalar Java 17
echo "☕ Instalando Java 17..."
sudo apt-get install -y openjdk-17-jdk

# Instalar Maven
echo "🔨 Instalando Maven..."
sudo apt-get install -y maven

# Instalar Git
echo "🐙 Instalando Git..."
sudo apt-get install -y git

# Descargar e instalar WildFly
echo "🚀 Instalando WildFly..."
WILDFLY_VERSION="31.0.0.Final"
cd /opt
sudo wget https://github.com/wildfly/wildfly/releases/download/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz
sudo tar xzf wildfly-$WILDFLY_VERSION.tar.gz
sudo mv wildfly-$WILDFLY_VERSION wildfly
sudo rm wildfly-$WILDFLY_VERSION.tar.gz

# Crear usuario wildfly
sudo groupadd -r wildfly
sudo useradd -r -g wildfly -d /opt/wildfly -s /sbin/nologin wildfly

# Dar permisos
sudo chown -R wildfly:wildfly /opt/wildfly
sudo chmod -R 755 /opt/wildfly

# Crear servicio systemd para WildFly
echo "⚙️ Configurando servicio WildFly..."
sudo tee /etc/systemd/system/wildfly.service > /dev/null <<EOF
[Unit]
Description=The WildFly Application Server
After=syslog.target network.target

[Service]
Type=simple
User=wildfly
Group=wildfly
ExecStart=/opt/wildfly/bin/standalone.sh -b=0.0.0.0 -bmanagement=0.0.0.0
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Habilitar y arrancar WildFly
sudo systemctl daemon-reload
sudo systemctl enable wildfly
sudo systemctl start wildfly

# Crear directorio para la aplicación
mkdir -p /home/ubuntu/gestor-identidades

# Configurar firewall
echo "🔥 Configurando firewall..."
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 8080/tcp  # WildFly HTTP
sudo ufw allow 9990/tcp  # WildFly Admin
sudo ufw --force enable

echo "✅ Configuración inicial completada!"
echo ""
echo "📋 Siguiente paso:"
echo "1. Configura el secret EC2_SSH_KEY en GitHub:"
echo "   - Ve a Settings > Secrets and variables > Actions"
echo "   - Crea un nuevo secret llamado 'EC2_SSH_KEY'"
echo "   - Copia el contenido de tu archivo KeyPairForSSH.pem"
echo ""
echo "2. La aplicación estará disponible en:"
echo "   http://ec2-3-134-91-53.us-east-2.compute.amazonaws.com:8080/GestorIdentidades"
