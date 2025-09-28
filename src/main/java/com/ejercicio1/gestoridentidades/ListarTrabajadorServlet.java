package com.ejercicio1.gestoridentidades;

import com.ejercicio1.business.EstadisticasSistema;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name="listarTrabajadorServlet", value="/listar-trabajadores")
public class ListarTrabajadorServlet extends HttpServlet {
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
        
        EstadisticasSistema estadisticas = trabajadorService.obtenerEstadisticas();

        request.setAttribute("trabajadores", trabajadores);
        request.setAttribute("totalTrabajadores", estadisticas.getTotalTrabajadores());
        request.setAttribute("trabajadoresActivos", estadisticas.getTrabajadoresActivos());
        request.setAttribute("especialidadMasComun", estadisticas.getEspecialidadMasComun());

        request.getRequestDispatcher("/listar-trabajadores.jsp").forward(request, response);
    }
}
