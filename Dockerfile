# Etapa de construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Primero copiar solo el pom.xml para cachear dependencias
COPY pom.xml .

# Descargar dependencias (esto se cacheará si pom.xml no cambia)
RUN mvn dependency:go-offline -B || true

# Ahora copiar el código fuente
COPY src ./src

# Construir la aplicación con reintentos
RUN mvn clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.retryHandler.requestSentEnabled=true \
    -Dmaven.wagon.http.serviceUnavailableRetryStrategy.retryInterval=10000 || \
    mvn clean package -DskipTests -o

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine

# Instalar curl y wget para health checks
RUN apk add --no-cache curl wget

# Crear directorio de aplicación
WORKDIR /app

# Descargar Jetty Runner con reintentos
RUN wget --tries=5 --retry-connrefused \
    https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/11.0.18/jetty-runner-11.0.18.jar \
    -O jetty-runner.jar

# Copiar el WAR desde la etapa de construcción
COPY --from=build /app/target/*.war /app/GestorIdentidades.war

# Exponer puerto dinámico de Railway
EXPOSE ${PORT}

# Configurar variables de entorno para optimización
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Crear script de inicio para mejor manejo de errores
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "Starting application on port $PORT"' >> /app/start.sh && \
    echo 'java $JAVA_OPTS -jar jetty-runner.jar --port $PORT GestorIdentidades.war' >> /app/start.sh && \
    chmod +x /app/start.sh

# Comando de inicio
CMD ["/app/start.sh"]
