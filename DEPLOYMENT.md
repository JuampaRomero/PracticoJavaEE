# Deployment de GestorIdentidades en AWS EC2

## Configuración Inicial

### 1. Preparar la instancia EC2

Conéctate a tu instancia EC2:
```bash
ssh -i "C:\Users\Usuario\Downloads\KeyPairForSSH.pem" ubuntu@ec2-18-118-115-30.us-east-2.compute.amazonaws.com
```

Una vez conectado, ejecuta el script de configuración:
```bash
# Copia el contenido de setup-ec2.sh y ejecútalo
wget https://raw.githubusercontent.com/TU_USUARIO/TU_REPOSITORIO/main/setup-ec2.sh
chmod +x setup-ec2.sh
./setup-ec2.sh
```

### 2. Configurar GitHub Actions

1. Ve a tu repositorio en GitHub
2. Ve a `Settings` > `Secrets and variables` > `Actions`
3. Crea un nuevo secret llamado `EC2_SSH_KEY`
4. Copia el contenido completo de tu archivo `KeyPairForSSH.pem` en el valor del secret

### 3. Configurar Base de Datos MySQL

En la instancia EC2:
```bash
# Instalar MySQL
sudo apt-get install -y mysql-server

# Configurar MySQL
sudo mysql -u root <<EOF
CREATE DATABASE gestoridentidades;
CREATE USER 'gestoruser'@'localhost' IDENTIFIED BY 'tu_contraseña_segura';
GRANT ALL PRIVILEGES ON gestoridentidades.* TO 'gestoruser'@'localhost';
FLUSH PRIVILEGES;
EOF
```

### 4. Configurar DataSource en WildFly

```bash
# Descargar driver MySQL
cd /opt/wildfly/standalone/deployments
sudo wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar
sudo chown wildfly:wildfly mysql-connector-java-8.0.33.jar

# Configurar datasource (editar standalone.xml manualmente o usar CLI)
```

## Uso del Deployment Automático

### Deployment Automático

Cada vez que hagas push a `main` o `master`, se ejecutará automáticamente el deployment:

```bash
git add .
git commit -m "Nueva funcionalidad"
git push origin main
```

### Ver logs del deployment

Ve a la pestaña `Actions` en tu repositorio de GitHub para ver el progreso del deployment.

### Acceder a la aplicación

Una vez completado el deployment:
- URL: http://ec2-18-118-115-30.us-east-2.compute.amazonaws.com:8080/GestorIdentidades
- Admin Console: http://ec2-18-118-115-30.us-east-2.compute.amazonaws.com:9990

## Troubleshooting

### Ver logs de WildFly
```bash
sudo tail -f /opt/wildfly/standalone/log/server.log
```

### Reiniciar WildFly manualmente
```bash
sudo systemctl restart wildfly
```

### Verificar estado del servicio
```bash
sudo systemctl status wildfly
```

## Estructura del Proyecto

```
GestorIdentidades/
├── .github/
│   └── workflows/
│       └── deploy.yml      # GitHub Actions workflow
├── src/                    # Código fuente
├── target/                 # Build artifacts (generado)
├── pom.xml                 # Configuración Maven
├── setup-ec2.sh           # Script configuración EC2
└── DEPLOYMENT.md          # Este archivo
```

## Notas de Seguridad

- **NUNCA** subas tu archivo `.pem` al repositorio
- Usa contraseñas seguras para la base de datos
- Considera usar HTTPS en producción
- Revisa regularmente los logs de seguridad
