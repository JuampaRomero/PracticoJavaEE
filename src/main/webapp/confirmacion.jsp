<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmación - Gestor de Identidades</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .success-message {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .success-message h2 {
            margin-top: 0;
        }
        .trabajador-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .info-row {
            display: flex;
            padding: 8px 0;
            border-bottom: 1px solid #dee2e6;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: bold;
            width: 200px;
            color: #495057;
        }
        .info-value {
            flex: 1;
            color: #212529;
        }
        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
        }
        .badge-success {
            background-color: #28a745;
            color: white;
        }
        .badge-secondary {
            background-color: #6c757d;
            color: white;
        }
        .actions {
            margin-top: 20px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            margin-right: 10px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            border: none;
        }
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
    </style>
</head>
<body>
    <div class="container">
        <c:if test="${not empty mensaje}">
            <div class="success-message">
                <h2>✓ ${mensaje}</h2>
            </div>
        </c:if>
        
        <c:if test="${not empty trabajador}">
            <h3>Detalles del Trabajador Registrado:</h3>
            <div class="trabajador-info">
                <div class="info-row">
                    <div class="info-label">Cédula:</div>
                    <div class="info-value">${trabajador.cedula}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">Nombre Completo:</div>
                    <div class="info-value">${trabajador.nombre} ${trabajador.apellido}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">Especialidad:</div>
                    <div class="info-value">${trabajador.especialidad}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">Matrícula Profesional:</div>
                    <div class="info-value">${trabajador.matriculaProfesional}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">Fecha de Ingreso:</div>
                    <div class="info-value">${trabajador.fechaIngreso}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">Estado:</div>
                    <div class="info-value">
                        <c:choose>
                            <c:when test="${trabajador.activo}">
                                <span class="badge badge-success">ACTIVO</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-secondary">INACTIVO</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:if>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/agregar-trabajador" class="btn btn-primary">Agregar Otro Trabajador</a>
            <a href="${pageContext.request.contextPath}/listar-trabajadores" class="btn btn-secondary">Ver Lista de Trabajadores</a>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-secondary">Volver al Inicio</a>
        </div>
    </div>
</body>
</html>
