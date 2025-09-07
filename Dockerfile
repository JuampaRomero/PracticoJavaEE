# Build stage
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage  
FROM openjdk:17-jdk-slim
WORKDIR /app

# Install wget
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Download Jetty Runner
RUN wget https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/11.0.18/jetty-runner-11.0.18.jar

# Copy WAR from build stage
COPY --from=build /app/target/*.war app.war

# Run application with root context
CMD ["sh", "-c", "java -jar jetty-runner-11.0.18.jar --port ${PORT:-8080} --path / app.war"]
