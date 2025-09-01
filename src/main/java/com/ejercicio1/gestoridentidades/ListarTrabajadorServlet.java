package com.ejercicio1.gestoridentidades;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name="listarTrabajadorServlet", value="/listar-trabajadores")
public class ListarTrabajadorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener la lista actualizada cada vez
        List<TrabajadorSalud> trabajadores = AgregarTrabajadorServlet.getTrabajadores();

        // Establecer la lista de trabajadores como atributo de la solicitud
        request.setAttribute("trabajadores", trabajadores);

        // Forward a la página JSP para mostrar la lista
        request.getRequestDispatcher("/listar-trabajadores.jsp").forward(request, response);
    }
}
