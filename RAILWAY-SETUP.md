# Railway Deployment Setup

## Configuración en Railway

### Paso 1: Crear proyecto en Railway
1. Ve a [Railway.app](https://railway.app)
2. Crea un nuevo proyecto
3. Selecciona "Deploy from GitHub repo"
4. Conecta tu repositorio: `JuampaRomero/PracticoJavaEE`

### Paso 2: Configurar variables de entorno en Railway
En el dashboard de Railway, agrega estas variables:
- `PORT`: Railway lo asigna automáticamente
- `JAVA_OPTS`: `-Xms256m -Xmx512m -XX:+UseG1GC`
- `TZ`: `America/Montevideo`

### Paso 3: Obtener tokens para GitHub Actions
1. En Railway, ve a tu cuenta → Tokens
2. Crea un nuevo token con nombre "GitHub Actions"
3. En tu proyecto Railway, obtén el Project ID y Service Name

### Paso 4: Configurar secretos en GitHub
Ve a tu repositorio en GitHub → Settings → Secrets and variables → Actions

Agrega estos secretos:
- `RAILWAY_TOKEN`: El token que creaste en Railway
- `RAILWAY_PROJECT_ID`: El ID de tu proyecto en Railway
- `RAILWAY_SERVICE_NAME`: El nombre del servicio (generalmente es el nombre del repo)

## Arquitectura del Deployment

### Stack tecnológico:
- **Java 17** con Eclipse Temurin
- **Jetty Runner 11** como servidor de aplicaciones (más ligero que Tomcat)
- **Maven** para construcción
- **Alpine Linux** para imagen mínima

### Flujo de deployment:
1. Push a main/master dispara GitHub Actions
2. GitHub Actions ejecuta tests
3. Si los tests pasan, construye el WAR
4. Railway detecta el push y construye el Docker image
5. Despliega automáticamente con Jetty Runner

### Optimizaciones implementadas:
- Multi-stage Docker build (reduce tamaño de imagen)
- JVM optimizada con G1GC
- Health checks configurados
- Restart policy para alta disponibilidad
- Build cache en GitHub Actions

## Endpoints disponibles

Una vez desplegado, tu aplicación estará disponible en:
- `https://[tu-app].up.railway.app/` - Homepage
- `https://[tu-app].up.railway.app/api/*` - REST endpoints
- `https://[tu-app].up.railway.app/[servlets]` - Servlets configurados

## Troubleshooting

### Si el deployment falla:
1. Revisa los logs en Railway Dashboard
2. Verifica que el WAR se construye correctamente: `mvn clean package`
3. Prueba localmente: 
   ```bash
   docker build -t gestor-test .
   docker run -p 8080:8080 -e PORT=8080 gestor-test
   ```

### Si la aplicación no responde:
1. Verifica el health check en Railway
2. Revisa los logs de Jetty Runner
3. Asegúrate que los servlets estén correctamente mapeados

### Comandos útiles:
```bash
# Ver logs en Railway CLI
railway logs

# Verificar estado
railway status

# Redeploy manual
railway up
```

## Monitoreo

Railway proporciona:
- Logs en tiempo real
- Métricas de CPU y memoria
- Alertas de deployment
- Historia de deployments

## Costos estimados

Con el plan gratuito de Railway:
- 500 horas de ejecución/mes
- 512MB RAM
- $5 USD de crédito mensual

Para esta aplicación Java EE:
- ~256-512MB RAM uso típico
- Costo estimado: $0-3 USD/mes
