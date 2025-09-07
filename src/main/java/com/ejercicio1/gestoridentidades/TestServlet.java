package com.ejercicio1.gestoridentidades;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="testServlet", value="/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.println("{");
        out.println("  \"status\": \"OK\",");
        out.println("  \"message\": \"Servlet funcionando correctamente\",");
        out.println("  \"timestamp\": " + System.currentTimeMillis() + ",");
        out.println("  \"endpoints\": [");
        out.println("    \"/test\",");
        out.println("    \"/api/trabajadores\",");
        out.println("    \"/api/trabajador/{ci}\",");
        out.println("    \"/listar-trabajadores\",");
        out.println("    \"/hello-servlet\"");
        out.println("  ]");
        out.println("}");
        out.flush();
    }
}
