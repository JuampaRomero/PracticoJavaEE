package com.ejercicio1.gestoridentidades;

import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name="buscarTrabajadorPorCIServlet", value="/buscar-trabajador")
public class BuscarTrabajadorPorCI extends HttpServlet {
    
    // Inyección del servicio de negocio
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retornamos el formulario de búsqueda al usuario
        request.getRequestDispatcher("/buscar-trabajador.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Configurar encoding para caracteres especiales
        request.setCharacterEncoding("UTF-8");
        
        // Obtener el parámetro de búsqueda
        String cedulaBuscada = request.getParameter("cedula");
        
        // Validar que se ingresó una cédula
        if (cedulaBuscada == null || cedulaBuscada.trim().isEmpty()) {
            request.setAttribute("error", "Por favor ingrese una cédula para buscar");
            request.getRequestDispatcher("/buscar-trabajador.jsp").forward(request, response);
            return;
        }
        
        // Limpiar la cédula de espacios
        cedulaBuscada = cedulaBuscada.trim();
        
        // Buscar el trabajador usando el servicio EJB
        TrabajadorSalud trabajadorEncontrado = trabajadorService.buscarPorCedula(cedulaBuscada);
        
        // Preparar respuesta según el resultado
        if (trabajadorEncontrado != null) {
            // Trabajador encontrado
            request.setAttribute("trabajador", trabajadorEncontrado);
            request.setAttribute("mensaje", "Trabajador encontrado exitosamente");
        } else {
            // Trabajador no encontrado
            request.setAttribute("error", "No se encontró ningún trabajador con la cédula: " + cedulaBuscada);
        }
        
        // Mantener la cédula buscada en el formulario
        request.setAttribute("cedulaBuscada", cedulaBuscada);
        
        // Redirigir a la página de resultados
        request.getRequestDispatcher("/buscar-trabajador.jsp").forward(request, response);
    }
}
