# 📡 API REST - Ejemplos de Consumo

## 🔗 Endpoints Disponibles

### 1. **Endpoints HTML (devuelven páginas web)**
- `GET /listar-trabajadores` - Lista HTML de trabajadores
- `GET /buscarPorCI` - Formulario de búsqueda
- `GET /agregar` - Formulario para agregar trabajador

### 2. **Endpoints REST (devuelven JSON)**
- `GET /api/trabajadores` - Lista todos los trabajadores
- `GET /api/trabajador/{ci}` - Busca trabajador por CI

## 🚀 Ejemplos con cURL

### Listar todos los trabajadores (JSON)
```bash
# Básico
curl https://tu-app.up.railway.app/api/trabajadores

# Con formato legible
curl https://tu-app.up.railway.app/api/trabajadores | json_pp

# Guardar respuesta
curl https://tu-app.up.railway.app/api/trabajadores -o trabajadores.json

# Ver headers
curl -i https://tu-app.up.railway.app/api/trabajadores

# Solo headers
curl -I https://tu-app.up.railway.app/api/trabajadores
```

### Buscar trabajador por CI
```bash
# Buscar por CI específica
curl https://tu-app.up.railway.app/api/trabajador/12345678

# Con formato legible
curl https://tu-app.up.railway.app/api/trabajador/12345678 | json_pp

# Manejo de errores (CI no existe)
curl -w "\nHTTP Status: %{http_code}\n" https://tu-app.up.railway.app/api/trabajador/99999999
```

### Ejemplos avanzados
```bash
# Con timeout
curl --max-time 10 https://tu-app.up.railway.app/api/trabajadores

# Con autenticación (si la implementas)
curl -H "Authorization: Bearer token123" https://tu-app.up.railway.app/api/trabajadores

# Verbose para debugging
curl -v https://tu-app.up.railway.app/api/trabajadores
```

## 📮 Ejemplos con Postman

### Configuración básica
1. **Method**: `GET`
2. **URL**: `https://tu-app.up.railway.app/api/trabajadores`

### Colección Postman
```json
{
  "info": {
    "name": "Gestor Identidades API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Listar Trabajadores",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "https://tu-app.up.railway.app/api/trabajadores",
          "protocol": "https",
          "host": ["tu-app", "up", "railway", "app"],
          "path": ["api", "trabajadores"]
        }
      }
    },
    {
      "name": "Buscar por CI",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "https://tu-app.up.railway.app/api/trabajador/12345678",
          "protocol": "https",
          "host": ["tu-app", "up", "railway", "app"],
          "path": ["api", "trabajador", "12345678"]
        }
      }
    }
  ]
}
```

### Tests en Postman
```javascript
// Test para listar trabajadores
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has required fields", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('success');
    pm.expect(jsonData).to.have.property('data');
    pm.expect(jsonData).to.have.property('estadisticas');
});

pm.test("Data is array", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data).to.be.an('array');
});
```

## 💻 Ejemplos con JavaScript/Fetch

```javascript
// Listar trabajadores
fetch('https://tu-app.up.railway.app/api/trabajadores')
  .then(response => response.json())
  .then(data => {
    console.log('Trabajadores:', data.data);
    console.log('Estadísticas:', data.estadisticas);
  })
  .catch(error => console.error('Error:', error));

// Buscar por CI
async function buscarTrabajador(ci) {
  try {
    const response = await fetch(`https://tu-app.up.railway.app/api/trabajador/${ci}`);
    const data = await response.json();
    
    if (data.success) {
      console.log('Trabajador encontrado:', data.data);
    } else {
      console.log('No encontrado:', data.message);
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

buscarTrabajador('12345678');
```

## 🐍 Ejemplos con Python

```python
import requests
import json

# Listar trabajadores
response = requests.get('https://tu-app.up.railway.app/api/trabajadores')
data = response.json()

print(f"Total trabajadores: {data['estadisticas']['totalTrabajadores']}")
for trabajador in data['data']:
    print(f"- {trabajador['nombre']} (CI: {trabajador['ci']})")

# Buscar por CI
ci = '12345678'
response = requests.get(f'https://tu-app.up.railway.app/api/trabajador/{ci}')
if response.status_code == 200:
    trabajador = response.json()['data']
    print(f"Encontrado: {trabajador['nombre']}")
else:
    print(f"No encontrado: CI {ci}")
```

## 📊 Estructura de Respuestas

### Lista de trabajadores
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "nombre": "Dr. Juan Pérez",
      "ci": "12345678",
      "especialidad": "Cardiología",
      "email": "juan.perez@hospital.com",
      "telefono": "099123456",
      "fechaIngreso": "2024-01-15",
      "activo": true
    }
  ],
  "estadisticas": {
    "totalTrabajadores": 10,
    "trabajadoresActivos": 8,
    "especialidadMasComun": "Medicina General"
  },
  "timestamp": 1699123456789
}
```

### Trabajador individual
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nombre": "Dr. Juan Pérez",
    "ci": "12345678",
    "especialidad": "Cardiología",
    "email": "juan.perez@hospital.com",
    "telefono": "099123456",
    "fechaIngreso": "2024-01-15",
    "activo": true
  }
}
```

### Error
```json
{
  "success": false,
  "error": "Mensaje de error",
  "statusCode": 404
}
```

## 🔧 Tips para Testing

1. **Usar jq para formatear JSON en terminal**:
   ```bash
   curl https://tu-app.up.railway.app/api/trabajadores | jq '.'
   ```

2. **Guardar respuestas para análisis**:
   ```bash
   curl https://tu-app.up.railway.app/api/trabajadores > response.json
   ```

3. **Medir tiempo de respuesta**:
   ```bash
   time curl https://tu-app.up.railway.app/api/trabajadores
   ```

4. **Headers personalizados**:
   ```bash
   curl -H "Accept: application/json" \
        -H "User-Agent: MiApp/1.0" \
        https://tu-app.up.railway.app/api/trabajadores
   ```
