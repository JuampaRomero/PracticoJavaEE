# Deployment en Railway - GestorIdentidades

## 🚀 Pasos para deployar en Railway

### 1. Crear cuenta en Railway (si no tienes una)
- Ve a https://railway.app
- Registrate con GitHub

### 2. Crear nuevo proyecto
1. Click en "New Project"
2. Selecciona "Deploy from GitHub repo"
3. Autoriza Railway para acceder a tu repositorio
4. Selecciona el repositorio: `JuampaRomero/PracticoJavaEE`

### 3. Configurar variables de entorno (si necesitas base de datos)
En el dashboard de Railway, ve a la pestaña "Variables" y agrega:

```
# Si necesitas MySQL en Railway:
MYSQL_URL=mysql://${{MYSQLUSER}}:${{MYSQLPASSWORD}}@${{MYSQLHOST}}:${{MYSQLPORT}}/${{MYSQLDATABASE}}

# O si usas una base de datos externa:
DATABASE_URL=tu_url_de_base_de_datos
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

### 4. Agregar servicio MySQL (opcional)
1. En tu proyecto Railway, click en "New"
2. Selecciona "Database" > "Add MySQL"
3. Railway creará automáticamente las variables de conexión

### 5. Deploy automático
- Railway detectará automáticamente el `Dockerfile`
- El build y deploy comenzará automáticamente
- Puedes ver los logs en tiempo real

### 6. Obtener la URL de tu aplicación
1. Una vez completado el deploy, ve a "Settings"
2. En la sección "Domains", click en "Generate Domain"
3. Tu aplicación estará disponible en: `https://tuapp.up.railway.app/GestorIdentidades`

## 🔧 Configuración adicional

### Puerto dinámico
Railway asigna un puerto dinámico a través de la variable `PORT`. Nuestro Dockerfile ya está configurado para usar el puerto 8080 internamente.

### Healthcheck
El healthcheck está configurado en `/` - asegúrate de que tu aplicación responda en esa ruta.

### Logs
Puedes ver los logs en tiempo real desde el dashboard de Railway o usando el CLI:

```bash
# Instalar Railway CLI
npm install -g @railway/cli

# Login
railway login

# Ver logs
railway logs
```

## 🎯 Ventajas de Railway

- **Deploy automático**: Cada push a GitHub se deploya automáticamente
- **SSL gratuito**: HTTPS incluido automáticamente
- **Escalado automático**: Railway escala tu app según la demanda
- **Base de datos incluida**: Puedes agregar MySQL/PostgreSQL con un click
- **Logs en tiempo real**: Ve exactamente qué está pasando con tu app
- **Sin downtime**: Los deploys son sin interrupciones

## 📝 Notas importantes

1. **Memoria**: Railway ofrece 512MB gratis, suficiente para aplicaciones pequeñas
2. **Builds**: Los builds pueden tardar 3-5 minutos la primera vez
3. **Dominio personalizado**: Puedes conectar tu propio dominio en el plan Pro
4. **Variables de entorno**: Se aplican automáticamente sin necesidad de rebuild
