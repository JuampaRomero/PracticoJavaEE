# Build stage
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage  
FROM quay.io/wildfly/wildfly:31.0.0.Final-jdk17

# Copiar el WAR al directorio de deployments
COPY --from=build /app/target/GestorIdentidades-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/GestorIdentidades.war

# Copiar script de inicio
COPY start.sh /opt/jboss/wildfly/bin/start.sh
RUN chmod +x /opt/jboss/wildfly/bin/start.sh

# Exponer el puerto (Railway usa PORT dinámicamente)
EXPOSE 8080

# Usar el script de inicio
CMD ["/opt/jboss/wildfly/bin/start.sh"]
