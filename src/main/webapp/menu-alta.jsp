<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema de Gestión - Alta de Trabajadores</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        .options-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }
        .option-card {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 25px;
            text-align: center;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .option-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .option-card h2 {
            color: #007bff;
            margin-bottom: 15px;
        }
        .option-card p {
            color: #6c757d;
            margin-bottom: 20px;
            min-height: 60px;
        }
        .btn {
            display: inline-block;
            padding: 10px 25px;
            text-decoration: none;
            color: white;
            border-radius: 3px;
            transition: background-color 0.3s;
            font-weight: bold;
        }
        .btn-primary {
            background-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-success {
            background-color: #28a745;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .info-section {
            background-color: #e9ecef;
            padding: 20px;
            border-radius: 5px;
            margin-top: 30px;
        }
        .info-section h3 {
            color: #495057;
            margin-top: 0;
        }
        .info-section ul {
            margin: 10px 0;
            padding-left: 20px;
        }
        .info-section li {
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Sistema de Gestión de Trabajadores de Salud</h1>
        
        <p style="text-align: center; color: #6c757d; font-size: 18px;">
            Seleccione el método de alta que desea utilizar:
        </p>
        
        <div class="options-grid">
            <div class="option-card">
                <h2>Alta Directa</h2>
                <p>
                    Procesa el alta inmediatamente con validación síncrona.
                    El trabajador será agregado al sistema de forma instantánea.
                </p>
                <a href="${pageContext.request.contextPath}/agregar-trabajador" class="btn btn-primary">
                    Alta Directa
                </a>
            </div>
            
            <div class="option-card">
                <h2>Alta vía JMS</h2>
                <p>
                    Envía la solicitud a una cola para procesamiento asíncrono.
                    El trabajador será agregado cuando el sistema procese el mensaje.
                </p>
                <a href="${pageContext.request.contextPath}/agregar-trabajador-jms" class="btn btn-success">
                    Alta vía JMS
                </a>
            </div>
        </div>
        
        <div class="info-section">
            <h3>¿Cuándo usar cada opción?</h3>
            <ul>
                <li><strong>Alta Directa:</strong> Cuando necesita confirmación inmediata y validación en tiempo real.</li>
                <li><strong>Alta vía JMS:</strong> Para procesar grandes volúmenes de datos o cuando no requiere confirmación inmediata.</li>
            </ul>
            
            <h3>Ventajas de JMS</h3>
            <ul>
                <li>Procesamiento asíncrono que no bloquea la aplicación</li>
                <li>Mayor escalabilidad para procesar múltiples solicitudes</li>
                <li>Tolerancia a fallos: los mensajes persisten hasta ser procesados</li>
                <li>Permite integración con sistemas externos</li>
            </ul>
        </div>
        
        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/" style="color: #6c757d; text-decoration: none;">
                ← Volver al inicio
            </a>
        </div>
    </div>
</body>
</html>