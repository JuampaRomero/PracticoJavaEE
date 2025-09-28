<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmación - Solicitud Enviada</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .success-message {
            background-color: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #c3e6cb;
        }
        .info-message {
            background-color: #d1ecf1;
            color: #0c5460;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #bee5eb;
        }
        .details {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .details h3 {
            margin-top: 0;
            color: #333;
        }
        .details p {
            margin: 5px 0;
        }
        .buttons {
            margin-top: 20px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-right: 10px;
            text-decoration: none;
            color: white;
            border-radius: 3px;
            transition: background-color 0.3s;
        }
        .btn-primary {
            background-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Solicitud Enviada Exitosamente</h1>
        
        <div class="success-message">
            <strong>¡Solicitud procesada!</strong> 
            <p>${mensaje}</p>
        </div>
        
        <div class="info-message">
            <strong>Información importante:</strong>
            <p>La solicitud de alta ha sido enviada a la cola de procesamiento. El trabajador será dado de alta de forma asíncrona.</p>
            <p>Este proceso puede tomar algunos segundos. Si el trabajador cumple con todas las validaciones de negocio, será agregado al sistema.</p>
        </div>
        
        <c:if test="${trabajador != null}">
            <div class="details">
                <h3>Datos enviados:</h3>
                <p><strong>Cédula:</strong> ${trabajador.cedula}</p>
                <p><strong>Nombre:</strong> ${trabajador.nombre}</p>
                <p><strong>Apellido:</strong> ${trabajador.apellido}</p>
                <p><strong>Especialidad:</strong> ${trabajador.especialidad}</p>
                <p><strong>Matrícula Profesional:</strong> ${trabajador.matriculaProfesional}</p>
                <p><strong>Fecha de Ingreso:</strong> ${trabajador.fechaIngreso}</p>
                <p><strong>Estado:</strong> ${trabajador.activo ? 'Activo' : 'Inactivo'}</p>
            </div>
        </c:if>
        
        <div class="buttons">
            <a href="${pageContext.request.contextPath}/agregar-trabajador-jms" class="btn btn-primary">
                Agregar otro trabajador (vía JMS)
            </a>
            <a href="${pageContext.request.contextPath}/agregar-trabajador" class="btn btn-secondary">
                Agregar otro trabajador (directo)
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">
                Volver al inicio
            </a>
        </div>
    </div>
</body>
</html>