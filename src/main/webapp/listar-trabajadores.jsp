<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lista de Trabajadores - Gestor de Identidades</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        .header {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        .header h1 {
            color: #333;
            margin: 0 0 10px 0;
        }
        .stats {
            display: flex;
            gap: 20px;
            margin-top: 15px;
        }
        .stat-box {
            background-color: #f8f9fa;
            padding: 15px 25px;
            border-radius: 6px;
            border-left: 4px solid #007bff;
        }
        .stat-box h3 {
            margin: 0;
            color: #007bff;
            font-size: 24px;
        }
        .stat-box p {
            margin: 5px 0 0 0;
            color: #666;
            font-size: 14px;
        }
        .actions {
            margin-bottom: 20px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            font-size: 16px;
            margin-right: 10px;
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
        .cards-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .card {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 20px;
            transition: transform 0.2s, box-shadow 0.2s;
            position: relative;
            overflow: hidden;
        }
        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 16px rgba(0,0,0,0.15);
        }
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: start;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 2px solid #f0f0f0;
        }
        .card-title {
            margin: 0;
            color: #333;
            font-size: 20px;
        }
        .card-subtitle {
            margin: 5px 0 0 0;
            color: #666;
            font-size: 14px;
        }
        .status-badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
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
        .card-body {
            margin-bottom: 15px;
        }
        .info-row {
            display: flex;
            margin-bottom: 10px;
        }
        .info-label {
            font-weight: bold;
            color: #555;
            width: 140px;
            font-size: 14px;
        }
        .info-value {
            color: #333;
            font-size: 14px;
            flex: 1;
        }
        .specialty-tag {
            display: inline-block;
            background-color: #e9ecef;
            color: #495057;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 13px;
            margin-top: 10px;
        }
        .no-results {
            text-align: center;
            padding: 60px 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .no-results h2 {
            color: #666;
            margin-bottom: 10px;
        }
        .no-results p {
            color: #999;
            margin-bottom: 20px;
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
    </style>
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link">← Volver al inicio</a>
        
        <div class="header">
            <h1>Lista de Trabajadores de Salud</h1>
            
            <div class="stats">
                <div class="stat-box">
                    <h3>${trabajadores.size()}</h3>
                    <p>Total de Trabajadores</p>
                </div>
                <div class="stat-box">
                    <h3>
                        <c:set var="activos" value="0" />
                        <c:forEach items="${trabajadores}" var="t">
                            <c:if test="${t.activo}">
                                <c:set var="activos" value="${activos + 1}" />
                            </c:if>
                        </c:forEach>
                        ${activos}
                    </h3>
                    <p>Trabajadores Activos</p>
                </div>
            </div>
        </div>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/agregar-trabajador" class="btn btn-primary">
                ➕ Agregar Nuevo Trabajador
            </a>
            <a href="${pageContext.request.contextPath}/buscar-trabajador" class="btn btn-secondary">
                🔍 Buscar Trabajador
            </a>
        </div>
        
        <c:choose>
            <c:when test="${empty trabajadores}">
                <div class="no-results">
                    <h2>No hay trabajadores registrados</h2>
                    <p>Aún no se han registrado trabajadores de salud en el sistema.</p>
                    <a href="${pageContext.request.contextPath}/agregar-trabajador" class="btn btn-primary">
                        Agregar el primer trabajador
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="cards-container">
                    <c:forEach items="${trabajadores}" var="trabajador">
                        <div class="card">
                            <div class="card-header">
                                <div>
                                    <h2 class="card-title">${trabajador.nombre} ${trabajador.apellido}</h2>
                                    <p class="card-subtitle">Cédula: ${trabajador.cedula}</p>
                                </div>
                                <c:choose>
                                    <c:when test="${trabajador.activo}">
                                        <span class="status-badge status-active">Activo</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge status-inactive">Inactivo</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="card-body">
                                <div class="info-row">
                                    <span class="info-label">Matrícula:</span>
                                    <span class="info-value">${trabajador.matriculaProfesional}</span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Fecha Ingreso:</span>
                                    <span class="info-value">${trabajador.fechaIngreso}</span>
                                </div>
                                <div class="info-row">
                                    <span class="info-label">Especialidad:</span>
                                    <span class="info-value">${trabajador.especialidad}</span>
                                </div>
                                
                                <span class="specialty-tag">
                                    <c:choose>
                                        <c:when test="${trabajador.especialidad == 'Medicina General'}">👨‍⚕️</c:when>
                                        <c:when test="${trabajador.especialidad == 'Pediatría'}">👶</c:when>
                                        <c:when test="${trabajador.especialidad == 'Cardiología'}">❤️</c:when>
                                        <c:when test="${trabajador.especialidad == 'Neurología'}">🧠</c:when>
                                        <c:when test="${trabajador.especialidad == 'Ginecología'}">👩‍⚕️</c:when>
                                        <c:when test="${trabajador.especialidad == 'Traumatología'}">🦴</c:when>
                                        <c:when test="${trabajador.especialidad == 'Oftalmología'}">👁️</c:when>
                                        <c:when test="${trabajador.especialidad == 'Dermatología'}">🩺</c:when>
                                        <c:when test="${trabajador.especialidad == 'Psiquiatría'}">🧠</c:when>
                                        <c:when test="${trabajador.especialidad == 'Enfermería'}">💉</c:when>
                                        <c:otherwise>🏥</c:otherwise>
                                    </c:choose>
                                    ${trabajador.especialidad}
                                </span>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
