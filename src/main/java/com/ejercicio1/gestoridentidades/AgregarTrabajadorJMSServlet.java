package com.ejercicio1.gestoridentidades;

import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import com.ejercicio1.jms.JMSMessageSender;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.jms.JMSException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet para agregar trabajadores de salud mediante JMS (procesamiento asíncrono).
 * Este servlet envía los datos a una cola JMS en lugar de realizar el alta directamente.
 */
@WebServlet(name="agregarTrabajadorJMSServlet", value="/agregar-trabajador-jms")
public class AgregarTrabajadorJMSServlet extends HttpServlet {
    
    @EJB
    private JMSMessageSender jmsMessageSender;
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Establecer flag para indicar que es el formulario JMS
        request.setAttribute("modoJMS", true);
        request.getRequestDispatcher("/agregar-trabajador.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Encoding de caracteres especiales
        request.setCharacterEncoding("UTF-8");

        // Obtenemos parametros del form
        String cedula = request.getParameter("cedula");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String especialidad = request.getParameter("especialidad");
        String matriculaProfesionalStr = request.getParameter("matricula");
        String fechaIngresoStr = request.getParameter("fechaIngreso");
        String activoStr = request.getParameter("activo");
        
        // Variables para manejar datos convertidos
        Integer matricula = null;
        LocalDate fechaIngreso = null;
        boolean activo = "true".equals(activoStr);
        
        // Lista de errores para validación básica de formato
        List<String> errores = new ArrayList<>();
        
        // Validación y conversión de matrícula
        try {
            if (matriculaProfesionalStr != null && !matriculaProfesionalStr.trim().isEmpty()) {
                matricula = Integer.parseInt(matriculaProfesionalStr);
            }
        } catch (NumberFormatException e) {
            errores.add("La matrícula debe ser un número válido");
        }
        
        // Validación y conversión de fecha
        try {
            if (fechaIngresoStr != null && !fechaIngresoStr.trim().isEmpty()) {
                fechaIngreso = LocalDate.parse(fechaIngresoStr);
            }
        } catch (DateTimeParseException e) {
            errores.add("Formato de fecha inválido");
        }
        
        // Validaciones básicas (sin usar el servicio de negocio completo)
        if (cedula == null || cedula.trim().isEmpty()) {
            errores.add("La cédula es obligatoria");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add("El nombre es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            errores.add("El apellido es obligatorio");
        }
        
        // Si hay errores de formato, volver al formulario
        if (!errores.isEmpty()) {
            volverAlFormularioConErrores(request, response, errores, cedula, nombre, 
                                       apellido, especialidad, matriculaProfesionalStr, 
                                       fechaIngresoStr, activo);
            return;
        }
        
        // Crear el nuevo trabajador
        TrabajadorSalud nuevoTrabajador = new TrabajadorSalud(
                cedula.trim(),
                nombre.trim(),
                apellido.trim(),
                especialidad != null ? especialidad.trim() : "",
                matricula,
                fechaIngreso,
                activo
        );
        
        try {
            // Enviar mensaje a la cola JMS
            jmsMessageSender.enviarMensajeAlta(nuevoTrabajador);
            
            // Establecer mensaje de éxito
            request.setAttribute("mensaje", "Solicitud de alta enviada exitosamente. El trabajador será procesado de forma asíncrona.");
            request.setAttribute("trabajador", nuevoTrabajador);
            request.setAttribute("modoJMS", true);
            
            // Redirigir a página de confirmación
            request.getRequestDispatcher("/confirmacion-jms.jsp").forward(request, response);
            
        } catch (JMSException e) {
            // Si hay un error al enviar el mensaje JMS
            errores.add("Error al enviar la solicitud: " + e.getMessage());
            volverAlFormularioConErrores(request, response, errores, cedula, nombre, 
                                       apellido, especialidad, matriculaProfesionalStr, 
                                       fechaIngresoStr, activo);
        }
    }
    
    /**
     * Método auxiliar para volver al formulario con errores
     */
    private void volverAlFormularioConErrores(HttpServletRequest request, 
                                             HttpServletResponse response,
                                             List<String> errores,
                                             String cedula, String nombre, String apellido,
                                             String especialidad, String matricula,
                                             String fechaIngreso, boolean activo) 
            throws ServletException, IOException {
        request.setAttribute("errores", errores);
        request.setAttribute("cedula", cedula);
        request.setAttribute("nombre", nombre);
        request.setAttribute("apellido", apellido);
        request.setAttribute("especialidad", especialidad);
        request.setAttribute("matricula", matricula);
        request.setAttribute("fechaIngreso", fechaIngreso);
        request.setAttribute("activo", activo);
        request.setAttribute("modoJMS", true);
        
        request.getRequestDispatcher("/agregar-trabajador.jsp").forward(request, response);
    }
}