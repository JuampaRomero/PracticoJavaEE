package com.ejercicio1.gestoridentidades;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="agregarTrabajadorServlet", value="/agregar-trabajador")
public class AgregarTrabajadorServlet extends HttpServlet {
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/agregar-trabajador.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String cedula = request.getParameter("cedula");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String especialidad = request.getParameter("especialidad");
        String matriculaProfesionalStr = request.getParameter("matricula");
        String fechaIngresoStr = request.getParameter("fechaIngreso");
        String activoStr = request.getParameter("activo");
        
        Integer matricula = null;
        LocalDate fechaIngreso = null;
        boolean activo = "true".equals(activoStr);
        
        List<String> errores = new ArrayList<>();
        try {
            if (matriculaProfesionalStr != null && !matriculaProfesionalStr.trim().isEmpty()) {
                matricula = Integer.parseInt(matriculaProfesionalStr);
            }
        } catch (NumberFormatException e) {
            errores.add("La matrícula debe ser un número válido");
        }
        
        try {
            if (fechaIngresoStr != null && !fechaIngresoStr.trim().isEmpty()) {
                fechaIngreso = LocalDate.parse(fechaIngresoStr);
            }
        } catch (DateTimeParseException e) {
            errores.add("Formato de fecha inválido");
        }
        
        if (!errores.isEmpty()) {
            volverAlFormularioConErrores(request, response, errores, cedula, nombre, 
                                       apellido, especialidad, matriculaProfesionalStr, 
                                       fechaIngresoStr, activo);
            return;
        }
        
        TrabajadorSalud nuevoTrabajador = new TrabajadorSalud(
                cedula != null ? cedula.trim() : "",
                nombre != null ? nombre.trim() : "",
                apellido != null ? apellido.trim() : "",
                especialidad != null ? especialidad.trim() : "",
                matricula,
                fechaIngreso,
                activo
        );
        
        try {
            trabajadorService.agregarTrabajador(nuevoTrabajador);
            
            request.setAttribute("mensaje", "Trabajador de salud agregado exitosamente");
            request.setAttribute("trabajador", nuevoTrabajador);
            
            request.getRequestDispatcher("/confirmacion.jsp").forward(request, response);
            
        } catch (BusinessException e) {
            errores.add(e.getMessage());
            volverAlFormularioConErrores(request, response, errores, cedula, nombre, 
                                       apellido, especialidad, matriculaProfesionalStr, 
                                       fechaIngresoStr, activo);
        }
    }
    
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
        
        request.getRequestDispatcher("/agregar-trabajador.jsp").forward(request, response);
    }
}

