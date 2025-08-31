<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Trabajador de Salud - Gestor de Identidades</title>
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
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            box-sizing: border-box;
        }
        input[type="checkbox"] {
            margin-right: 5px;
        }
        .checkbox-label {
            font-weight: normal;
            display: inline;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-right: 10px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #545b62;
        }
        .error-messages {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .error-messages ul {
            margin: 0;
            padding-left: 20px;
        }
        .required {
            color: red;
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
        
        <h1>Agregar Nuevo Trabajador de Salud</h1>
        
        <!-- Mostrar errores si existen -->
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
        
        <form action="${pageContext.request.contextPath}/agregar-trabajador" method="post">
            <div class="form-group">
                <label for="cedula">Cédula <span class="required">*</span></label>
                <input type="text" 
                       id="cedula" 
                       name="cedula" 
                       value="${cedula}" 
                       placeholder="Ej: 12345678"
                       required>
            </div>
            
            <div class="form-group">
                <label for="nombre">Nombre <span class="required">*</span></label>
                <input type="text" 
                       id="nombre" 
                       name="nombre" 
                       value="${nombre}"
                       placeholder="Ej: Juan"
                       required>
            </div>
            
            <div class="form-group">
                <label for="apellido">Apellido <span class="required">*</span></label>
                <input type="text" 
                       id="apellido" 
                       name="apellido" 
                       value="${apellido}"
                       placeholder="Ej: Pérez"
                       required>
            </div>
            
            <div class="form-group">
                <label for="especialidad">Especialidad <span class="required">*</span></label>
                <select id="especialidad" name="especialidad" required>
                    <option value="">-- Seleccione una especialidad --</option>
                    <option value="Medicina General" ${especialidad == 'Medicina General' ? 'selected' : ''}>Medicina General</option>
                    <option value="Pediatría" ${especialidad == 'Pediatría' ? 'selected' : ''}>Pediatría</option>
                    <option value="Cardiología" ${especialidad == 'Cardiología' ? 'selected' : ''}>Cardiología</option>
                    <option value="Neurología" ${especialidad == 'Neurología' ? 'selected' : ''}>Neurología</option>
                    <option value="Ginecología" ${especialidad == 'Ginecología' ? 'selected' : ''}>Ginecología</option>
                    <option value="Traumatología" ${especialidad == 'Traumatología' ? 'selected' : ''}>Traumatología</option>
                    <option value="Oftalmología" ${especialidad == 'Oftalmología' ? 'selected' : ''}>Oftalmología</option>
                    <option value="Dermatología" ${especialidad == 'Dermatología' ? 'selected' : ''}>Dermatología</option>
                    <option value="Psiquiatría" ${especialidad == 'Psiquiatría' ? 'selected' : ''}>Psiquiatría</option>
                    <option value="Anestesiología" ${especialidad == 'Anestesiología' ? 'selected' : ''}>Anestesiología</option>
                    <option value="Radiología" ${especialidad == 'Radiología' ? 'selected' : ''}>Radiología</option>
                    <option value="Enfermería" ${especialidad == 'Enfermería' ? 'selected' : ''}>Enfermería</option>
                    <option value="Otra" ${especialidad == 'Otra' ? 'selected' : ''}>Otra</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="matricula">Matrícula Profesional <span class="required">*</span></label>
                <input type="number" 
                       id="matricula" 
                       name="matricula" 
                       value="${matricula}"
                       placeholder="Ej: 12345"
                       min="1"
                       required>
            </div>
            
            <div class="form-group">
                <label for="fechaIngreso">Fecha de Ingreso <span class="required">*</span></label>
                <input type="date" 
                       id="fechaIngreso" 
                       name="fechaIngreso" 
                       value="${fechaIngreso}"
                       max="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                       required>
            </div>
            
            <div class="form-group">
                <label>
                    <input type="checkbox" 
                           id="activo" 
                           name="activo" 
                           value="true"
                           ${activo ? 'checked' : ''}>
                    <span class="checkbox-label">Trabajador Activo</span>
                </label>
            </div>
            
            <div class="form-group">
                <button type="submit" class="btn">Guardar Trabajador</button>
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-secondary" style="text-decoration: none; display: inline-block;">Cancelar</a>
            </div>
        </form>
    </div>
</body>
</html>
