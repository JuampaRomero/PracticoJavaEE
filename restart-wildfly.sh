#!/bin/bash

echo "========================================"
echo "  REINICIANDO WILDFLY EN AWS"
echo "========================================"

# Detener y eliminar contenedor anterior si existe
echo "1. Limpiando contenedores anteriores..."
sudo docker stop wildfly-app 2>/dev/null
sudo docker rm wildfly-app 2>/dev/null

# Limpiar espacio
echo "2. Limpiando espacio en disco..."
sudo docker system prune -f

# Verificar espacio disponible
echo "3. Espacio disponible:"
df -h /

# Iniciar WildFly con configuración optimizada para t2.micro
echo "4. Iniciando WildFly con memoria optimizada..."
sudo docker run -d \
  --name wildfly-app \
  --restart unless-stopped \
  -p 8080:8080 \
  -p 9990:9990 \
  -e JAVA_OPTS='-Xms256m -Xmx512m -Djava.net.preferIPv4Stack=true -Djboss.bind.address=0.0.0.0 -Djboss.bind.address.management=0.0.0.0' \
  -e WILDFLY_USER=admin \
  -e WILDFLY_PASS=Admin123! \
  quay.io/wildfly/wildfly:33.0.2.Final-jdk21

# Esperar a que WildFly inicie
echo "5. Esperando que WildFly inicie..."
sleep 20

# Verificar que esté corriendo
echo "6. Verificando estado..."
sudo docker ps

# Copiar el WAR si existe
if [ -f ~/GestorIdentidades-1.0-SNAPSHOT.war ]; then
    echo "7. Desplegando aplicación..."
    sudo docker cp ~/GestorIdentidades-1.0-SNAPSHOT.war wildfly-app:/opt/jboss/wildfly/standalone/deployments/
    
    # Crear archivo de marca para auto-deploy
    sudo docker exec wildfly-app touch /opt/jboss/wildfly/standalone/deployments/GestorIdentidades-1.0-SNAPSHOT.war.dodeploy
    
    echo "8. Aplicación desplegada. Esperando 30 segundos..."
    sleep 30
    
    echo "9. Verificando despliegue..."
    sudo docker exec wildfly-app ls -la /opt/jboss/wildfly/standalone/deployments/
else
    echo "ADVERTENCIA: No se encontró el archivo WAR en ~/GestorIdentidades-1.0-SNAPSHOT.war"
fi

# Mostrar logs
echo "10. Últimas líneas de log:"
sudo docker logs --tail 30 wildfly-app

# Obtener IP pública
PUBLIC_IP=$(curl -s http://checkip.amazonaws.com)

echo ""
echo "========================================"
echo "  CONFIGURACIÓN COMPLETADA"
echo "========================================"
echo "IP Pública: $PUBLIC_IP"
echo ""
echo "URLs disponibles:"
echo "  Aplicación: http://$PUBLIC_IP:8080/GestorIdentidades-1.0-SNAPSHOT/"
echo "  Admin Console: http://$PUBLIC_IP:9990"
echo ""
echo "Credenciales Admin Console:"
echo "  Usuario: admin"
echo "  Contraseña: Admin123!"
echo "========================================"
