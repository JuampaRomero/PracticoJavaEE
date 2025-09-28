<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Trabajador de Salud</title>
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
            padding: 30px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
            color: #555;
        }
        input[type="text"],
        input[type="number"],
        input[type="date"],
        select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .checkbox-group {
            display: flex;
            align-items: center;
        }
        input[type="checkbox"] {
            margin-right: 10px;
        }
        .error-messages {
            background-color: #f8d7da;
            color: #721c24;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
        .error-messages ul {
            margin: 5px 0;
            padding-left: 20px;
        }
        .button-group {
            margin-top: 20px;
            text-align: center;
        }
        .btn {
            padding: 10px 20px;
            margin: 0 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s;
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
        .info-banner {
            background-color: #d1ecf1;
            color: #0c5460;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            border: 1px solid #bee5eb;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Agregar Trabajador de Salud</h1>
        
        <c:if test="${modoJMS}">
            <div class="info-banner">
                <strong>Modo JMS:</strong> El trabajador será procesado de forma asíncrona
            </div>
        </c:if>
        
        <c:if test="${not empty errores}">
            <div class="error-messages">
                <strong>Por favor corrija los siguientes errores:</strong>
                <ul>
                    <c:forEach items="${errores}" var="error">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}${modoJMS ? '/agregar-trabajador-jms' : '/agregar-trabajador'}" method="post">
            <div class="form-group">
                <label for="cedula">Cédula: <span style="color: red;">*</span></label>
                <input type="text" id="cedula" name="cedula" value="${cedula}" required>
            </div>
            
            <div class="form-group">
                <label for="nombre">Nombre: <span style="color: red;">*</span></label>
                <input type="text" id="nombre" name="nombre" value="${nombre}" required>
            </div>
            
            <div class="form-group">
                <label for="apellido">Apellido: <span style="color: red;">*</span></label>
                <input type="text" id="apellido" name="apellido" value="${apellido}" required>
            </div>
            
            <div class="form-group">
                <label for="especialidad">Especialidad:</label>
                <select id="especialidad" name="especialidad">
                    <option value="">-- Seleccione una especialidad --</option>
                    <option value="Medicina General" ${especialidad == 'Medicina General' ? 'selected' : ''}>Medicina General</option>
                    <option value="Pediatría" ${especialidad == 'Pediatría' ? 'selected' : ''}>Pediatría</option>
                    <option value="Cardiología" ${especialidad == 'Cardiología' ? 'selected' : ''}>Cardiología</option>
                    <option value="Neurología" ${especialidad == 'Neurología' ? 'selected' : ''}>Neurología</option>
                    <option value="Traumatología" ${especialidad == 'Traumatología' ? 'selected' : ''}>Traumatología</option>
                    <option value="Ginecología" ${especialidad == 'Ginecología' ? 'selected' : ''}>Ginecología</option>
                    <option value="Oftalmología" ${especialidad == 'Oftalmología' ? 'selected' : ''}>Oftalmología</option>
                    <option value="Dermatología" ${especialidad == 'Dermatología' ? 'selected' : ''}>Dermatología</option>
                    <option value="Psiquiatría" ${especialidad == 'Psiquiatría' ? 'selected' : ''}>Psiquiatría</option>
                    <option value="Enfermería" ${especialidad == 'Enfermería' ? 'selected' : ''}>Enfermería</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="matricula">Matrícula Profesional:</label>
                <input type="number" id="matricula" name="matricula" value="${matricula}">
            </div>
            
            <div class="form-group">
                <label for="fechaIngreso">Fecha de Ingreso:</label>
                <input type="date" id="fechaIngreso" name="fechaIngreso" value="${fechaIngreso}">
            </div>
            
            <div class="form-group">
                <div class="checkbox-group">
                    <input type="checkbox" id="activo" name="activo" value="true" ${activo ? 'checked' : ''}>
                    <label for="activo">Activo</label>
                </div>
            </div>
            
            <div class="button-group">
                <button type="submit" class="btn btn-primary">
                    ${modoJMS ? 'Enviar a Cola JMS' : 'Guardar Trabajador'}
                </button>
                <button type="button" class="btn btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/'">
                    Cancelar
                </button>
            </div>
        </form>
        
        <div style="margin-top: 30px; text-align: center;">
            <a href="${pageContext.request.contextPath}/menu-alta.jsp" style="color: #6c757d; text-decoration: none;">
                ← Volver al menú de opciones
            </a>
        </div>
    </div>
</body>
</html>