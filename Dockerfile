# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM quay.io/wildfly/wildfly:31.0.0.Final-jdk17

# Copiar el WAR generado
COPY --from=build /app/target/GestorIdentidades.war /opt/jboss/wildfly/standalone/deployments/

# Exponer el puerto que Railway asignará dinámicamente
EXPOSE 8080

# Configurar WildFly para escuchar en todas las interfaces y usar el puerto de Railway
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
