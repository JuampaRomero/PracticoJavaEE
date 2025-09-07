#!/bin/bash
# Script para iniciar WildFly con el puerto de Railway

# Si Railway proporciona un puerto, usarlo
if [ -n "$PORT" ]; then
    echo "Starting WildFly on port $PORT"
    exec /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Djboss.http.port=$PORT
else
    echo "Starting WildFly on default port 8080"
    exec /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0
fi
