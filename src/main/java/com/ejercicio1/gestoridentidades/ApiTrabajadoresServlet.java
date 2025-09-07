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
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="apiTrabajadoresServlet", value="/api/trabajadores")
public class ApiTrabajadoresServlet extends HttpServlet {
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
            
            out.println("{");
            out.println("  \"success\": true,");
            out.println("  \"data\": [");
            
            for (int i = 0; i < trabajadores.size(); i++) {
                TrabajadorSalud t = trabajadores.get(i);
                out.println("    {");
                out.println("      \"cedula\": \"" + escapeJson(t.getCedula()) + "\",");
                out.println("      \"nombre\": \"" + escapeJson(t.getNombre()) + "\",");
                out.println("      \"apellido\": \"" + escapeJson(t.getApellido()) + "\",");
                out.println("      \"especialidad\": \"" + escapeJson(t.getEspecialidad()) + "\",");
                out.println("      \"matriculaProfesional\": " + t.getMatriculaProfesional() + ",");
                out.println("      \"activo\": " + t.isActivo());
                out.print("    }");
                if (i < trabajadores.size() - 1) {
                    out.println(",");
                } else {
                    out.println();
                }
            }
            
            out.println("  ]");
            out.println("}");
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{");
            out.println("  \"success\": false,");
            out.println("  \"error\": \"" + escapeJson(e.getMessage()) + "\"");
            out.println("}");
        }
        
        out.flush();
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
