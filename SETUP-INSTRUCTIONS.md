# Instrucciones de Configuración EC2 para GestorIdentidades

## Paso 1: Conectarse a EC2

```bash
ssh -i "C:\Users\Usuario\Downloads\KeyPairForSSH.pem" ubuntu@18.118.115.30
```

## Paso 2: Copiar y ejecutar el script de configuración

Una vez conectado a EC2, ejecuta estos comandos:

```bash
# Crear el script
cat > setup-ec2.sh << 'SCRIPT_END'
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

# Crear servicio systemd para WildFly
echo "⚙️ Configurando servicio WildFly systemd..."
sudo tee /etc/systemd/system/wildfly.service > /dev/null <<'EOF'
[Unit]
Description=WildFly Application Server
After=syslog.target network.target

[Service]
Type=simple
User=wildfly
Group=wildfly
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
ExecStart=/opt/wildfly/bin/standalone.sh -b=0.0.0.0 -bmanagement=0.0.0.0
Restart=always
RestartSec=20
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# Habilitar y arrancar servicios
sudo systemctl daemon-reload
sudo systemctl enable wildfly
sudo systemctl start wildfly

# Crear directorio para la aplicación
mkdir -p /home/ubuntu/gestor-identidades

# Clonar repositorio
cd /home/ubuntu
git clone https://github.com/JuampaRomero/PracticoJavaEE.git gestor-identidades || true

# Configurar firewall
echo "🔥 Configurando firewall..."
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 8080/tcp  # WildFly HTTP
sudo ufw allow 9990/tcp  # WildFly Admin
sudo ufw --force enable

echo "✅ Configuración inicial completada!"
echo ""
echo "🌐 URLs:"
echo "  - Aplicación: http://18.118.115.30:8080/GestorIdentidades"
echo "  - Admin: http://18.118.115.30:9990"
SCRIPT_END

# Hacer el script ejecutable
chmod +x setup-ec2.sh

# Ejecutar el script
./setup-ec2.sh
```

## Paso 3: Configurar GitHub Actions

1. Ve a tu repositorio en GitHub: https://github.com/JuampaRomero/PracticoJavaEE
2. Ve a `Settings` → `Secrets and variables` → `Actions`
3. Clic en `New repository secret`
4. Nombre: `EC2_SSH_KEY`
5. Valor: Copia TODO el contenido del archivo `C:\Users\Usuario\Downloads\KeyPairForSSH.pem`

## Paso 4: Verificar el deployment

```bash
# Ver estado de WildFly
sudo systemctl status wildfly

# Ver logs
sudo journalctl -u wildfly -f

# Verificar que el puerto esté escuchando
sudo netstat -tlnp | grep 8080
```

## Comandos útiles para administración

```bash
# Reiniciar WildFly
sudo systemctl restart wildfly

# Ver logs en tiempo real
sudo tail -f /opt/wildfly/standalone/log/server.log

# Conectar a MySQL
mysql -u gestoruser -pGestorPass2024! gestoridentidades

# Ver deployments
ls -la /opt/wildfly/standalone/deployments/
```

## Solución de problemas comunes

### Si WildFly no arranca:
```bash
# Verificar logs
sudo journalctl -xe -u wildfly

# Verificar permisos
ls -la /opt/wildfly/

# Reiniciar manualmente
sudo -u wildfly /opt/wildfly/bin/standalone.sh
```

### Si la aplicación no se despliega:
```bash
# Verificar el WAR
ls -la /home/ubuntu/gestor-identidades/target/

# Copiar manualmente
sudo cp /home/ubuntu/gestor-identidades/target/GestorIdentidades.war /opt/wildfly/standalone/deployments/
sudo chown wildfly:wildfly /opt/wildfly/standalone/deployments/GestorIdentidades.war
```
