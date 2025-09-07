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

# Railway necesita que escuchemos en el puerto proporcionado por PORT
ENV JAVA_OPTS="-Djboss.http.port=${PORT:-8080}"

# Exponer el puerto por defecto
EXPOSE ${PORT:-8080}

# Iniciar WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
