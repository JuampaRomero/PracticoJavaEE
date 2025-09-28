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
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/buscar-trabajador.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String cedulaBuscada = request.getParameter("cedula");
        
        if (cedulaBuscada == null || cedulaBuscada.trim().isEmpty()) {
            request.setAttribute("error", "Por favor ingrese una cédula para buscar");
            request.getRequestDispatcher("/buscar-trabajador.jsp").forward(request, response);
            return;
        }
        
        cedulaBuscada = cedulaBuscada.trim();
        
        TrabajadorSalud trabajadorEncontrado = trabajadorService.buscarPorCedula(cedulaBuscada);
        
        if (trabajadorEncontrado != null) {
            request.setAttribute("trabajador", trabajadorEncontrado);
            request.setAttribute("mensaje", "Trabajador encontrado exitosamente");
        } else {
            request.setAttribute("error", "No se encontró ningún trabajador con la cédula: " + cedulaBuscada);
        }
        
        request.setAttribute("cedulaBuscada", cedulaBuscada);
        
        request.getRequestDispatcher("/buscar-trabajador.jsp").forward(request, response);
    }
}
