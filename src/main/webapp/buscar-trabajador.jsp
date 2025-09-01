<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Buscar Trabajador - Gestor de Identidades</title>
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
        h1 {
            color: #333;
            border-bottom: 2px solid #007bff;
            padding-bottom: 10px;
        }
        .search-form {
            margin: 30px 0;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            font-weight: bold;
            margin-bottom: 8px;
            color: #555;
            font-size: 16px;
        }
        input[type="text"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
            transition: border-color 0.3s;
        }
        input[type="text"]:focus {
            outline: none;
            border-color: #007bff;
        }
        .search-hint {
            color: #666;
            font-size: 14px;
            margin-top: 5px;
        }
        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
            text-decoration: none;
            display: inline-block;
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
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .success-message {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .result-card {
            background-color: #f8f9fa;
            border: 2px solid #007bff;
            border-radius: 8px;
            padding: 25px;
            margin-top: 30px;
        }
        .result-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #dee2e6;
        }
        .result-title {
            margin: 0;
            color: #333;
            font-size: 24px;
        }
        .status-badge {
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
            font-weight: bold;
            text-transform: uppercase;
        }
        .status-active {
            background-color: #d4edda;
            color: #155724;
        }
        .status-inactive {
            background-color: #f8d7da;
            color: #721c24;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 15px;
        }
        .info-item {
            padding: 10px 0;
        }
        .info-label {
            font-weight: bold;
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }
        .info-value {
            color: #333;
            font-size: 16px;
        }
        .nav-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .nav-link:hover {
            text-decoration: underline;
        }
        .search-icon {
            font-size: 3em;
            text-align: center;
            color: #007bff;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link">← Volver al inicio</a>
        
        <h1>Buscar Trabajador por Cédula</h1>
        
        <div class="search-icon">🔍</div>
        
        <!-- Mostrar mensajes de error si existen -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>
        
        <!-- Mostrar mensaje de éxito si existe -->
        <c:if test="${not empty mensaje}">
            <div class="success-message">
                ${mensaje}
            </div>
        </c:if>
        
        <!-- Formulario de búsqueda -->
        <div class="search-form">
            <form action="${pageContext.request.contextPath}/buscar-trabajador" method="post">
                <div class="form-group">
                    <label for="cedula">Ingrese la cédula del trabajador:</label>
                    <input type="text" 
                           id="cedula" 
                           name="cedula" 
                           value="${cedulaBuscada}"
                           placeholder="Ej: 12345678"
                           required
                           autofocus>
                    <p class="search-hint">Ingrese el número de cédula sin puntos ni guiones</p>
                </div>
                
                <button type="submit" class="btn btn-primary">Buscar Trabajador</button>
                <a href="${pageContext.request.contextPath}/listar-trabajadores" class="btn btn-secondary">Ver Todos</a>
            </form>
        </div>
        
        <!-- Mostrar resultado si se encontró un trabajador -->
        <c:if test="${not empty trabajador}">
            <div class="result-card">
                <div class="result-header">
                    <h2 class="result-title">${trabajador.nombre} ${trabajador.apellido}</h2>
                    <c:choose>
                        <c:when test="${trabajador.activo}">
                            <span class="status-badge status-active">Activo</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-badge status-inactive">Inactivo</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Cédula de Identidad</div>
                        <div class="info-value">${trabajador.cedula}</div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-label">Matrícula Profesional</div>
                        <div class="info-value">${trabajador.matriculaProfesional}</div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-label">Especialidad</div>
                        <div class="info-value">${trabajador.especialidad}</div>
                    </div>
                    
                    <div class="info-item">
                        <div class="info-label">Fecha de Ingreso</div>
                        <div class="info-value">${trabajador.fechaIngreso}</div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</body>
</html>
