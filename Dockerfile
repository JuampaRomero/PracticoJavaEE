# Build stage
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - Using Tomcat
FROM tomcat:10-jdk17

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR to Tomcat webapps as ROOT.war
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Create a script to configure Tomcat port from Railway
RUN echo '#!/bin/bash' > /usr/local/tomcat/bin/setenv.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Dserver.port=$PORT"' >> /usr/local/tomcat/bin/setenv.sh && \
    chmod +x /usr/local/tomcat/bin/setenv.sh

# Replace port in server.xml dynamically
CMD ["sh", "-c", "sed -i 's/8080/'"$PORT"'/g' /usr/local/tomcat/conf/server.xml && catalina.sh run"]
