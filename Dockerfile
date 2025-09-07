# Etapa de construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para health checks
RUN apk add --no-cache curl

# Crear directorio de aplicación
WORKDIR /app

# Descargar Jetty Runner
RUN wget https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/11.0.18/jetty-runner-11.0.18.jar -O jetty-runner.jar

# Copiar el WAR desde la etapa de construcción
COPY --from=build /app/target/*.war /app/GestorIdentidades.war

# Exponer puerto dinámico de Railway
EXPOSE ${PORT}

# Configurar variables de entorno para optimización
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Comando de inicio usando la variable PORT de Railway
CMD java $JAVA_OPTS -jar jetty-runner.jar --port $PORT GestorIdentidades.war
