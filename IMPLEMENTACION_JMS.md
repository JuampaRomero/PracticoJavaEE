# Implementación JMS para Alta de TrabajadorSalud

## Descripción General

Esta implementación agrega capacidad de procesamiento asíncrono mediante JMS (Java Message Service) para el alta de trabajadores de salud, manteniendo la funcionalidad existente de alta directa.

## Componentes Implementados

### 1. Message Driven Bean (MDB)
**Archivo:** `src/main/java/com/ejercicio1/mdb/TrabajadorSaludMDB.java`
- Escucha mensajes de la cola `queue_alta_trabajadorsalud`
- Parsea mensajes con formato: `cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo`
- Utiliza el servicio EJB existente para realizar el alta con todas las validaciones de negocio

### 2. Servicio JMS Sender
**Archivo:** `src/main/java/com/ejercicio1/jms/JMSMessageSender.java`
- EJB Stateless para enviar mensajes a la cola
- Formatea los datos del trabajador según el formato esperado por el MDB
- Maneja conexiones JMS de forma segura

### 3. Configuración de Cola JMS
**Archivo:** `src/main/webapp/WEB-INF/jms-destinations.xml`
- Define la cola `queue_alta_trabajadorsalud` en WildFly
- Configura los nombres JNDI para acceso local y remoto

### 4. Servlet para Alta vía JMS
**Archivo:** `src/main/java/com/ejercicio1/gestoridentidades/AgregarTrabajadorJMSServlet.java`
- Endpoint alternativo: `/agregar-trabajador-jms`
- Realiza validaciones básicas antes de enviar a la cola
- Mantiene la misma interfaz que el servlet original

### 5. Integración con JSF
**Archivo modificado:** `src/main/java/com/ejercicio1/web/beans/TrabajadorBean.java`
- Nuevo método `guardarTrabajadorJMS()`
- Flag `usarJMS` para alternar entre procesamiento directo y asíncrono
- Mantiene el método original `guardarTrabajador()` sin cambios

### 6. Cliente Standalone JMS
**Archivo:** `src/main/java/com/ejercicio1/cliente/ClienteJMS.java`
- Aplicación Java independiente para enviar mensajes a la cola
- Interfaz de línea de comandos interactiva
- Método programático para integración con otros sistemas

### 7. Interfaces Web
- **menu-alta.jsp**: Menú principal para elegir entre alta directa o vía JMS
- **confirmacion-jms.jsp**: Página de confirmación específica para alta asíncrona

## Configuración y Despliegue

### 1. Dependencias Maven
Las siguientes dependencias fueron agregadas al `pom.xml`:
```xml
<dependency>
    <groupId>jakarta.jms</groupId>
    <artifactId>jakarta.jms-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope>
</dependency>
```

### 2. Configuración de WildFly
La cola JMS se configura automáticamente al desplegar la aplicación gracias al archivo `jms-destinations.xml`.

### 3. Compilación y Despliegue
```bash
mvn clean package
# Copiar el WAR generado a la carpeta deployments de WildFly
```

## Uso de la Funcionalidad

### Opción 1: Via Web (Servlet)
1. Acceder a `/menu-alta.jsp`
2. Seleccionar "Alta vía JMS"
3. Completar el formulario
4. El sistema enviará la solicitud a la cola para procesamiento asíncrono

### Opción 2: Via JSF
En la interfaz JSF, el bean `trabajadorBean` expone:
- `guardarTrabajador()`: Alta directa (comportamiento original)
- `guardarTrabajadorJMS()`: Alta vía mensajería

### Opción 3: Cliente Standalone
```bash
java -cp ".:wildfly-client-all.jar" com.ejercicio1.cliente.ClienteJMS
```

## Formato del Mensaje JMS

Los mensajes deben seguir este formato:
```
cedula|nombre|apellido|especialidad|matriculaProfesional|fechaIngreso|activo
```

Ejemplo:
```
12345678|Juan|Pérez|Cardiología|98765|2024-01-15|true
```

## Ventajas de la Implementación JMS

1. **Procesamiento Asíncrono**: No bloquea la aplicación durante el alta
2. **Escalabilidad**: Permite procesar múltiples altas en paralelo
3. **Tolerancia a Fallos**: Los mensajes persisten hasta ser procesados
4. **Desacoplamiento**: Facilita la integración con sistemas externos
5. **Mantiene Funcionalidad Existente**: El alta directa sigue funcionando sin cambios

## Monitoreo y Logs

El MDB registra información en los logs del servidor:
- Mensajes recibidos
- Altas exitosas
- Errores de validación o procesamiento

Para ver los logs en WildFly:
```bash
tail -f standalone/log/server.log
```

## Consideraciones de Seguridad

- La cola está configurada para acceso local y remoto
- Para producción, considerar:
  - Autenticación y autorización en las colas
  - Encriptación de mensajes sensibles
  - Límites de tamaño de cola y timeouts

## Troubleshooting

### Error: Cola no encontrada
- Verificar que WildFly esté ejecutándose
- Confirmar que el archivo `jms-destinations.xml` está en `WEB-INF`
- Revisar logs de despliegue

### Error: Mensaje no procesado
- Verificar formato del mensaje (7 campos separados por |)
- Revisar logs del MDB para errores de validación
- Confirmar que el servicio EJB está disponible