package com.ejercicio1.gestoridentidades;

import com.ejercicio1.entities.TrabajadorSalud;
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

    private static final List<TrabajadorSalud> trabajadores = new ArrayList<>();


    // Métodos auxiliares para validación (temporales hasta implementar EJB)
    private boolean existeCedula(String cedula) {
        return trabajadores.stream()
                .anyMatch(t -> t.getCedula().equals(cedula));
    }

    private boolean existeMatricula(Integer matricula) {
        return trabajadores.stream()
                .anyMatch(t -> t.getMatriculaProfesional().equals(matricula));
    }

    // Método temporal para obtener la lista (útil para pruebas)
    public static List<TrabajadorSalud> getTrabajadores() {
        return new ArrayList<>(trabajadores);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Retornamos el formulario de agregar trabajador al usuario
        request.getRequestDispatcher("/agregar-trabajador.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Encoding de caracteres especiales
        request.setCharacterEncoding("UTF-8");

        //Lista de errores de validacion
        List<String> errores = new ArrayList<>();

        //Obtenemos parametros del form
        String cedula = request.getParameter("cedula");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String especialidad = request.getParameter("especialidad");
        String matriculaProfesionalStr = request.getParameter("matricula");
        String fechaIngresoStr = request.getParameter("fechaIngreso");
        String activoStr = request.getParameter("activo");

        // Validaciones
        if (cedula == null || cedula.trim().isEmpty()) {
            errores.add("La cédula es obligatoria");
        } else if (existeCedula(cedula)) {
            errores.add("Ya existe un trabajador con esa cédula");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add("El nombre es obligatorio");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            errores.add("El apellido es obligatorio");
        }

        if (especialidad == null || especialidad.trim().isEmpty()) {
            errores.add("La especialidad es obligatoria");
        }

        Integer matricula = null;
        try {
            if (matriculaProfesionalStr != null && !matriculaProfesionalStr.trim().isEmpty()) {
                matricula = Integer.parseInt(matriculaProfesionalStr);
                if (matricula <= 0) {
                    errores.add("La matrícula debe ser un número positivo");
                } else if (existeMatricula(matricula)) {
                    errores.add("Ya existe un trabajador con esa matrícula profesional");
                }
            } else {
                errores.add("La matrícula profesional es obligatoria");
            }
        } catch (NumberFormatException e) {
            errores.add("La matrícula debe ser un número válido");
        }

        LocalDate fechaIngreso = null;
        try {
            if (fechaIngresoStr != null && !fechaIngresoStr.trim().isEmpty()) {
                fechaIngreso = LocalDate.parse(fechaIngresoStr);
                if (fechaIngreso.isAfter(LocalDate.now())) {
                    errores.add("La fecha de ingreso no puede ser futura");
                }
            } else {
                errores.add("La fecha de ingreso es obligatoria");
            }
        } catch (DateTimeParseException e) {
            errores.add("Formato de fecha inválido");
        }

        boolean activo = "true".equals(activoStr);

        // Si hay errores, volver al formulario con los errores
        if (!errores.isEmpty()) {
            request.setAttribute("errores", errores);
            request.setAttribute("cedula", cedula);
            request.setAttribute("nombre", nombre);
            request.setAttribute("apellido", apellido);
            request.setAttribute("especialidad", especialidad);
            request.setAttribute("matricula", matriculaProfesionalStr);
            request.setAttribute("fechaIngreso", fechaIngresoStr);
            request.setAttribute("activo", activo);

            request.getRequestDispatcher("/agregar-trabajador.jsp").forward(request, response);
            return;
        }

        // Crear el nuevo trabajador
        TrabajadorSalud nuevoTrabajador = new TrabajadorSalud(
                cedula.trim(),
                nombre.trim(),
                apellido.trim(),
                especialidad.trim(),
                matricula,
                fechaIngreso,
                activo
        );

        // Agregar a la lista (temporal, luego será mediante EJB)
        trabajadores.add(nuevoTrabajador);

        // Establecer mensaje de éxito
        request.setAttribute("mensaje", "Trabajador de salud agregado exitosamente");
        request.setAttribute("trabajador", nuevoTrabajador);

        // Redirigir a página de confirmación
        request.getRequestDispatcher("/confirmacion.jsp").forward(request, response);
    }
}

