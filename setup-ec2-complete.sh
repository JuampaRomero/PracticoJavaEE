#!/bin/bash
# Script completo de configuración para EC2 con servicio systemd
# Ejecutar una sola vez en la instancia EC2

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

# Instalar MySQL
echo "🗄️ Instalando MySQL..."
sudo apt-get install -y mysql-server

# Configurar MySQL
echo "⚙️ Configurando MySQL..."
sudo mysql -u root <<EOF
CREATE DATABASE IF NOT EXISTS gestoridentidades;
CREATE USER IF NOT EXISTS 'gestoruser'@'localhost' IDENTIFIED BY 'GestorPass2024!';
GRANT ALL PRIVILEGES ON gestoridentidades.* TO 'gestoruser'@'localhost';
FLUSH PRIVILEGES;
EOF

# Descargar e instalar WildFly
echo "🚀 Instalando WildFly..."
WILDFLY_VERSION="31.0.0.Final"
cd /opt
sudo wget -q https://github.com/wildfly/wildfly/releases/download/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz
sudo tar xzf wildfly-$WILDFLY_VERSION.tar.gz
sudo mv wildfly-$WILDFLY_VERSION wildfly
sudo rm wildfly-$WILDFLY_VERSION.tar.gz

# Crear usuario wildfly
sudo groupadd -r wildfly || true
sudo useradd -r -g wildfly -d /opt/wildfly -s /sbin/nologin wildfly || true

# Dar permisos
sudo chown -R wildfly:wildfly /opt/wildfly
sudo chmod -R 755 /opt/wildfly

# Descargar driver MySQL
echo "📥 Descargando driver MySQL..."
cd /opt/wildfly/modules/system/layers/base
sudo mkdir -p com/mysql/main
cd com/mysql/main
sudo wget -q https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
sudo mv mysql-connector-j-8.0.33.jar mysql-connector-java.jar

# Crear module.xml para MySQL
sudo tee module.xml > /dev/null <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="com.mysql">
    <resources>
        <resource-root path="mysql-connector-java.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
EOF

sudo chown -R wildfly:wildfly /opt/wildfly/modules

# Crear archivo de configuración standalone personalizado
echo "📝 Creando configuración WildFly personalizada..."
sudo cp /opt/wildfly/standalone/configuration/standalone.xml /opt/wildfly/standalone/configuration/standalone-custom.xml

# Crear script de configuración WildFly CLI
cat > /tmp/wildfly-config.cli <<'EOF'
# Conectar
connect

# Agregar driver MySQL
/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql)

# Agregar datasource
data-source add \
    --name=GestorIdentidadesDS \
    --jndi-name=java:/GestorIdentidadesDS \
    --driver-name=mysql \
    --connection-url=jdbc:mysql://localhost:3306/gestoridentidades?useSSL=false&allowPublicKeyRetrieval=true \
    --user-name=gestoruser \
    --password=GestorPass2024! \
    --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker \
    --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter

# Habilitar datasource
data-source enable --name=GestorIdentidadesDS

# Salir
exit
EOF

# Crear servicio systemd para WildFly
echo "⚙️ Configurando servicio WildFly systemd..."
sudo tee /etc/systemd/system/wildfly.service > /dev/null <<'EOF'
[Unit]
Description=WildFly Application Server
After=syslog.target network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
User=wildfly
Group=wildfly
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="JBOSS_HOME=/opt/wildfly"
ExecStartPre=/bin/sleep 10
ExecStart=/opt/wildfly/bin/standalone.sh -b=0.0.0.0 -bmanagement=0.0.0.0 -c standalone-custom.xml
ExecStop=/opt/wildfly/bin/jboss-cli.sh --connect command=:shutdown
Restart=always
RestartSec=20
StandardOutput=journal
StandardError=journal
SyslogIdentifier=wildfly

[Install]
WantedBy=multi-user.target
EOF

# Crear servicio para la aplicación GestorIdentidades
echo "🎯 Creando servicio systemd para GestorIdentidades..."
sudo tee /etc/systemd/system/gestor-identidades.service > /dev/null <<'EOF'
[Unit]
Description=GestorIdentidades Deployment Manager
After=wildfly.service
Requires=wildfly.service

[Service]
Type=oneshot
RemainAfterExit=yes
User=ubuntu
WorkingDirectory=/home/ubuntu/gestor-identidades
ExecStart=/usr/local/bin/deploy-gestor.sh
StandardOutput=journal
StandardError=journal
SyslogIdentifier=gestor-identidades

[Install]
WantedBy=multi-user.target
EOF

# Crear script de deployment
echo "📄 Creando script de deployment..."
sudo tee /usr/local/bin/deploy-gestor.sh > /dev/null <<'EOF'
#!/bin/bash
set -e

APP_DIR="/home/ubuntu/gestor-identidades"
WILDFLY_HOME="/opt/wildfly"
WAR_NAME="GestorIdentidades.war"

# Esperar a que WildFly esté listo
echo "Esperando a que WildFly esté listo..."
for i in {1..30}; do
    if /opt/wildfly/bin/jboss-cli.sh --connect --command=":read-attribute(name=server-state)" 2>/dev/null | grep -q "running"; then
        echo "WildFly está listo"
        break
    fi
    echo "Esperando... ($i/30)"
    sleep 2
done

# Si existe el WAR, desplegarlo
if [ -f "$APP_DIR/target/$WAR_NAME" ]; then
    echo "Desplegando aplicación..."
    sudo cp "$APP_DIR/target/$WAR_NAME" "$WILDFLY_HOME/standalone/deployments/"
    sudo chown wildfly:wildfly "$WILDFLY_HOME/standalone/deployments/$WAR_NAME"
    echo "Deployment completado"
else
    echo "WAR no encontrado en $APP_DIR/target/$WAR_NAME"
fi
EOF

sudo chmod +x /usr/local/bin/deploy-gestor.sh

# Habilitar servicios
sudo systemctl daemon-reload
sudo systemctl enable mysql
sudo systemctl enable wildfly
sudo systemctl enable gestor-identidades

# Iniciar MySQL primero
sudo systemctl start mysql

# Iniciar WildFly
sudo systemctl start wildfly

# Esperar a que WildFly esté completamente iniciado
echo "⏳ Esperando a que WildFly inicie completamente..."
sleep 30

# Configurar WildFly con CLI
echo "🔧 Aplicando configuración WildFly..."
/opt/wildfly/bin/jboss-cli.sh --file=/tmp/wildfly-config.cli || true

# Reiniciar WildFly con la nueva configuración
sudo systemctl restart wildfly

# Crear directorio para la aplicación y clonar repositorio
echo "📂 Preparando aplicación..."
mkdir -p /home/ubuntu/gestor-identidades
cd /home/ubuntu
if [ ! -d "gestor-identidades/.git" ]; then
    git clone https://github.com/JuampaRomero/PracticoJavaEE.git gestor-identidades
fi

cd gestor-identidades
# Encontrar el directorio del proyecto
PROJECT_DIR=$(find . -name "pom.xml" -type f | head -1 | xargs dirname)
if [ -n "$PROJECT_DIR" ]; then
    cd "$PROJECT_DIR"
    mvn clean package
fi

# Configurar firewall
echo "🔥 Configurando firewall..."
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 8080/tcp  # WildFly HTTP
sudo ufw allow 9990/tcp  # WildFly Admin
sudo ufw allow 3306/tcp  # MySQL
sudo ufw --force enable

# Crear script de monitoreo
echo "📊 Creando script de monitoreo..."
sudo tee /usr/local/bin/check-gestor.sh > /dev/null <<'EOF'
#!/bin/bash
echo "=== Estado de GestorIdentidades ==="
echo ""
echo "🔷 MySQL:"
sudo systemctl status mysql --no-pager | grep "Active:"
echo ""
echo "🔷 WildFly:"
sudo systemctl status wildfly --no-pager | grep "Active:"
echo ""
echo "🔷 Deployments:"
ls -la /opt/wildfly/standalone/deployments/*.war 2>/dev/null || echo "No hay deployments"
ls -la /opt/wildfly/standalone/deployments/*.deployed 2>/dev/null || echo "No hay deployments exitosos"
echo ""
echo "🔷 Logs recientes:"
sudo journalctl -u wildfly -n 20 --no-pager
EOF

sudo chmod +x /usr/local/bin/check-gestor.sh

echo "✅ Configuración completada!"
echo ""
echo "📋 Comandos útiles:"
echo "  - Ver estado: sudo systemctl status wildfly"
echo "  - Ver logs: sudo journalctl -u wildfly -f"
echo "  - Reiniciar: sudo systemctl restart wildfly"
echo "  - Monitoreo: /usr/local/bin/check-gestor.sh"
echo ""
echo "🌐 URLs:"
echo "  - Aplicación: http://18.118.115.30:8080/GestorIdentidades"
echo "  - Admin: http://18.118.115.30:9990"
echo ""
echo "🔑 Base de datos:"
echo "  - Usuario: gestoruser"
echo "  - Contraseña: GestorPass2024!"
echo "  - Base de datos: gestoridentidades"
