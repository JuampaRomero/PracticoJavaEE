package com.ejercicio1.gestoridentidades;

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
import java.util.Map;

@WebServlet(name="buscarTrabajadorRestServlet", value="/api/trabajador/*")
public class BuscarTrabajadorRestServlet extends HttpServlet {
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // Obtener CI del path: /api/trabajador/{ci}
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, "CI no especificada", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String ci = pathInfo.substring(1); // Remover el slash inicial
        
        try {
            TrabajadorSalud trabajador = trabajadorService.buscarPorCI(ci);
            
            Map<String, Object> jsonResponse = new HashMap<>();
            
            if (trabajador != null) {
                jsonResponse.put("success", true);
                jsonResponse.put("data", trabajador);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Trabajador no encontrado con CI: " + ci);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, jsonResponse);
            out.flush();
            
        } catch (Exception e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void sendError(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        errorResponse.put("statusCode", statusCode);
        
        PrintWriter out = response.getWriter();
        objectMapper.writeValue(out, errorResponse);
        out.flush();
    }
}
