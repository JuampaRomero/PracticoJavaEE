<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestor de Identidades - Sistema de Trabajadores de Salud</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }
        .header {
            background-color: #007bff;
            color: white;
            padding: 20px 0;
            text-align: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
        }
        .header p {
            margin: 5px 0 0 0;
            font-size: 1.1em;
            opacity: 0.9;
        }
        .container {
            max-width: 1000px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }
        .menu-card {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .menu-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 16px rgba(0,0,0,0.15);
        }
        .menu-card h2 {
            color: #333;
            margin-top: 0;
        }
        .menu-card p {
            color: #666;
            margin: 15px 0;
            line-height: 1.5;
        }
        .menu-card a {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 12px 30px;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.2s;
            font-weight: bold;
        }
        .menu-card a:hover {
            background-color: #0056b3;
        }
        .icon {
            font-size: 3em;
            margin-bottom: 15px;
        }
        .stats {
            background-color: #e9ecef;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            text-align: center;
        }
        .stats h3 {
            margin-top: 0;
            color: #495057;
        }
        .footer {
            text-align: center;
            padding: 20px;
            color: #666;
            margin-top: 50px;
            border-top: 1px solid #dee2e6;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Gestor de Identidades</h1>
        <p>Sistema de Gestión de Trabajadores del Sector Salud</p>
    </div>
    
    <div class="container">
        <div class="stats">
            <h3>Bienvenido al Sistema de Gestión</h3>
            <p>Administre de forma eficiente la información de los trabajadores de salud de su institución</p>
        </div>
        
        <div class="menu-grid">
            <div class="menu-card">
                <div class="icon">➕</div>
                <h2>Agregar Trabajador</h2>
                <p>Registre un nuevo trabajador de salud en el sistema con todos sus datos profesionales</p>
                <a href="${pageContext.request.contextPath}/agregar-trabajador">Agregar Nuevo</a>
            </div>
            
            <div class="menu-card">
                <div class="icon">📋</div>
                <h2>Listar Trabajadores</h2>
                <p>Visualice la lista completa de todos los trabajadores registrados en el sistema</p>
                <a href="${pageContext.request.contextPath}/listar-trabajadores">Ver Lista</a>
            </div>
            
            <div class="menu-card">
                <div class="icon">🔍</div>
                <h2>Buscar Trabajador</h2>
                <p>Encuentre rápidamente un trabajador específico utilizando diferentes criterios de búsqueda</p>
                <a href="${pageContext.request.contextPath}/buscar-trabajador">Buscar</a>
            </div>
            
            <div class="menu-card">
                <div class="icon">🧪</div>
                <h2>Servlet de Prueba</h2>
                <p>Acceda al servlet de ejemplo para verificar el funcionamiento del sistema</p>
                <a href="${pageContext.request.contextPath}/hello-servlet">Hello Servlet</a>
            </div>
        </div>
    </div>
    
    <div class="footer">
        <p>&copy; 2024 Gestor de Identidades - Desarrollado con Jakarta EE</p>
    </div>
</body>
</html>
