#!/bin/bash

# Script simplificado para instalar WildFly sin Docker
echo "========================================"
echo "  INSTALACIÓN DE WILDFLY SIN DOCKER"
echo "========================================"

# Detener Docker si existe
echo "Limpiando Docker..."
sudo systemctl stop docker 2>/dev/null
sudo docker stop $(sudo docker ps -aq) 2>/dev/null
sudo docker rm $(sudo docker ps -aq) 2>/dev/null

# Crear swap
echo "Configurando swap..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
fi

# Instalar Java
echo "Instalando Java 17..."
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk-headless wget

# Descargar e instalar WildFly
echo "Descargando WildFly..."
cd /tmp
wget -q https://github.com/wildfly/wildfly/releases/download/33.0.2.Final/wildfly-33.0.2.Final.tar.gz
sudo tar xf wildfly-33.0.2.Final.tar.gz -C /opt/
sudo ln -sfn /opt/wildfly-33.0.2.Final /opt/wildfly

# Crear usuario
sudo groupadd -r wildfly 2>/dev/null
sudo useradd -r -g wildfly -d /opt/wildfly -s /sbin/nologin wildfly 2>/dev/null
sudo chown -RH wildfly:wildfly /opt/wildfly

# Configurar memoria
sudo tee /opt/wildfly/bin/standalone.conf > /dev/null << 'EOF'
JAVA_OPTS="-Xms256m -Xmx768m -XX:MaxMetaspaceSize=256m"
JAVA_OPTS="$JAVA_OPTS -XX:+UseSerialGC"
JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
JAVA_OPTS="$JAVA_OPTS -Djboss.bind.address=0.0.0.0"
JAVA_OPTS="$JAVA_OPTS -Djboss.bind.address.management=0.0.0.0"
EOF

# Crear servicio
sudo tee /etc/systemd/system/wildfly.service > /dev/null << 'EOF'
[Unit]
Description=WildFly
After=network.target

[Service]
Type=simple
User=wildfly
Group=wildfly
ExecStart=/opt/wildfly/bin/standalone.sh
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

# Crear usuario admin
sudo -u wildfly /opt/wildfly/bin/add-user.sh -u admin -p Admin123! --silent

# Iniciar servicio
sudo systemctl daemon-reload
sudo systemctl start wildfly
sudo systemctl enable wildfly

# Esperar inicio
sleep 30

# Desplegar app si existe
if [ -f ~/GestorIdentidades-1.0-SNAPSHOT.war ]; then
    sudo cp ~/GestorIdentidades-1.0-SNAPSHOT.war /opt/wildfly/standalone/deployments/
    sudo chown wildfly:wildfly /opt/wildfly/standalone/deployments/GestorIdentidades-1.0-SNAPSHOT.war
fi

# Mostrar URLs
IP=$(curl -s http://checkip.amazonaws.com)
echo ""
echo "========================================"
echo "WILDFLY INSTALADO!"
echo "========================================"
echo "App: http://$IP:8080/GestorIdentidades-1.0-SNAPSHOT/"
echo "Admin: http://$IP:9990 (admin/Admin123!)"
echo "========================================"
