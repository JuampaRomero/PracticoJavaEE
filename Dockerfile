# Build stage
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - Using WildFly
FROM quay.io/wildfly/wildfly:31.0.0.Final-jdk17

# Switch to root user for setup
USER root

# Copy the WAR file to WildFly deployments directory
COPY --from=build /app/target/*.war /opt/jboss/wildfly/standalone/deployments/ROOT.war

# Create a script to configure WildFly for Railway's dynamic port
RUN echo '#!/bin/bash' > /opt/jboss/wildfly/bin/configure-port.sh && \
    echo 'if [ -n "$PORT" ]; then' >> /opt/jboss/wildfly/bin/configure-port.sh && \
    echo '  /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Djboss.http.port=$PORT' >> /opt/jboss/wildfly/bin/configure-port.sh && \
    echo 'else' >> /opt/jboss/wildfly/bin/configure-port.sh && \
    echo '  /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0' >> /opt/jboss/wildfly/bin/configure-port.sh && \
    echo 'fi' >> /opt/jboss/wildfly/bin/configure-port.sh && \
    chmod +x /opt/jboss/wildfly/bin/configure-port.sh

# Switch back to jboss user
USER jboss

# Expose the port
EXPOSE 8080

# Run WildFly with the configured port
CMD ["/opt/jboss/wildfly/bin/configure-port.sh"]
