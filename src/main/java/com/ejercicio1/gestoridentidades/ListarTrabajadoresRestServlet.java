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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name="listarTrabajadoresRestServlet", value="/api/trabajadores")
public class ListarTrabajadoresRestServlet extends HttpServlet {
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configurar respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Permitir CORS (para consumo desde otras aplicaciones)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        
        try {
            // Obtener datos
            List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
            EstadisticasSistema estadisticas = trabajadorService.obtenerEstadisticas();
            
            // Crear respuesta JSON
            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", true);
            jsonResponse.put("data", trabajadores);
            jsonResponse.put("estadisticas", Map.of(
                "totalTrabajadores", estadisticas.getTotalTrabajadores(),
                "trabajadoresActivos", estadisticas.getTrabajadoresActivos(),
                "especialidadMasComun", estadisticas.getEspecialidadMasComun()
            ));
            jsonResponse.put("timestamp", System.currentTimeMillis());
            
            // Escribir JSON
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, jsonResponse);
            out.flush();
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, errorResponse);
            out.flush();
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Manejo de preflight CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
