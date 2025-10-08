package PrestadorSalud.web;

import PrestadorSalud.messaging.PrestadorAltaProducer;
import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;
import PrestadorSalud.service.PrestadorSaludServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/prestadores")
public class PrestadorSaludServlet extends HttpServlet {

    @EJB
    private PrestadorSaludServiceLocal service;

    @EJB
    private PrestadorAltaProducer altaProducer;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombre");
        List<PrestadorSalud> prestadores = (nombre != null && !nombre.isBlank())
                ? service.buscarPorNombre(nombre)
                : service.obtenerPrestadores();
        req.setAttribute("prestadores", prestadores);
        req.getRequestDispatcher("/prestadores.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombre");
        String direccion = req.getParameter("direccion");
        TipoPrestador tipo = TipoPrestador.valueOf(req.getParameter("tipo"));
        PrestadorSalud prestador = new PrestadorSalud(nombre, direccion, tipo);
        try {
            altaProducer.enviarAlta(prestador);
            resp.sendRedirect(req.getContextPath() + "/prestadores");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            req.setAttribute("error", ex.getMessage());
            doGet(req, resp);
        }
    }
}
