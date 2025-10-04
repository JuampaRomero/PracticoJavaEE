package com.ejercicio1.business;

import com.ejercicio1.dao.TrabajadorSaludDAOLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TrabajadorSaludService implements TrabajadorSaludServiceLocal, TrabajadorSaludServiceRemote {
    
    private static final Logger LOGGER = Logger.getLogger(TrabajadorSaludService.class.getName());
    
    @EJB
    private TrabajadorSaludDAOLocal trabajadorDAO;
    
    // Reglas de negocio:
    // 1. Cédula: 7-10 dígitos
    // 2. Nombre/Apellido: mínimo 2 caracteres
    // 3. Matrícula: > 1000
    // 4. Fecha ingreso: 1950 - presente
    // 5. Nuevos trabajadores activos por defecto
    @Override
    public void agregarTrabajador(TrabajadorSalud trabajador) throws BusinessException {
        validarTrabajador(trabajador);
        
        if (trabajador.getFechaIngreso().isAfter(LocalDate.now().minusDays(30))) {
            trabajador.setActivo(true);
        }
        
        try {
            trabajadorDAO.agregar(trabajador);
            LOGGER.info("Trabajador agregado exitosamente: " + trabajador.getCedula());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Error al agregar trabajador: " + e.getMessage());
        }
    }

    public void eliminarTrabajador(String cedula) throws BusinessException {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new BusinessException("La cédula es obligatoria para eliminar un trabajador");

        }

        TrabajadorSalud existente = trabajadorDAO.buscarPorCedula(cedula);
        if (existente == null) {
            throw new BusinessException("No se encontró un trabajador con la cédula: " + cedula);
        }

        try {
            trabajadorDAO.eliminar(existente);
            LOGGER.info("Trabajador eliminado exitosamente: " + cedula);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Error al eliminar trabajador: " + e.getMessage());
        }
    }
    
    @Override
    public void validarTrabajador(TrabajadorSalud trabajador) throws BusinessException {
        if (trabajador == null) {
            throw new BusinessException("El trabajador no puede ser null");
        }
        
        if (trabajador.getCedula() == null || trabajador.getCedula().trim().isEmpty()) {
            throw new BusinessException("La cédula es obligatoria");
        }
        
        String cedula = trabajador.getCedula().trim();
        if (!cedula.matches("\\d{7,10}")) {
            throw new BusinessException("La cédula debe contener entre 7 y 10 dígitos numéricos");
        }
        
        if (trabajador.getNombre() == null || trabajador.getNombre().trim().length() < 2) {
            throw new BusinessException("El nombre debe tener al menos 2 caracteres");
        }
        
        if (trabajador.getApellido() == null || trabajador.getApellido().trim().length() < 2) {
            throw new BusinessException("El apellido debe tener al menos 2 caracteres");
        }
        
        if (trabajador.getEspecialidad() == null || trabajador.getEspecialidad().trim().isEmpty()) {
            throw new BusinessException("La especialidad es obligatoria");
        }
        
        if (trabajador.getMatriculaProfesional() == null) {
            throw new BusinessException("La matrícula profesional es obligatoria");
        }
        
        if (trabajador.getMatriculaProfesional() <= 1000) {
            throw new BusinessException("La matrícula profesional debe ser mayor a 1000");
        }
        
        if (trabajador.getFechaIngreso() == null) {
            throw new BusinessException("La fecha de ingreso es obligatoria");
        }
        
        LocalDate fechaMinima = LocalDate.of(1950, 1, 1);
        LocalDate fechaMaxima = LocalDate.now();
        
        if (trabajador.getFechaIngreso().isBefore(fechaMinima)) {
            throw new BusinessException("La fecha de ingreso no puede ser anterior a 1950");
        }
        
        if (trabajador.getFechaIngreso().isAfter(fechaMaxima)) {
            throw new BusinessException("La fecha de ingreso no puede ser futura");
        }
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<TrabajadorSalud> obtenerTodos() {
        return trabajadorDAO.obtenerTodos();
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<TrabajadorSalud> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            return List.of();
        }
        
        List<TrabajadorSalud> todos = trabajadorDAO.obtenerTodos();
        if (todos == null) {
            return List.of();
        }
        
        return todos.stream()
                .filter(t -> t.getEspecialidad().equalsIgnoreCase(especialidad.trim()))
                .collect(Collectors.toList());
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public TrabajadorSalud buscarPorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        
        if (!cedula.trim().matches("\\d{7,10}")) {
            return null;
        }
        
        return trabajadorDAO.buscarPorCedula(cedula);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public EstadisticasSistema obtenerEstadisticas() {
        EstadisticasSistema stats = new EstadisticasSistema();
        
        List<TrabajadorSalud> todos = trabajadorDAO.obtenerTodos();
        int total = todos.size();
        int activos = trabajadorDAO.contarTrabajadoresActivos();
        
        stats.setTotalTrabajadores(total);
        stats.setTrabajadoresActivos(activos);
        stats.setTrabajadoresInactivos(total - activos);
        
        if (total > 0) {
            stats.setPorcentajeActivos((activos * 100.0) / total);
        } else {
            stats.setPorcentajeActivos(0.0);
        }
        
        if (!todos.isEmpty()) {
            Map<String, Long> especialidadCount = todos.stream()
                    .collect(Collectors.groupingBy(
                            TrabajadorSalud::getEspecialidad,
                            Collectors.counting()
                    ));
            
            String especialidadMasComun = especialidadCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            
            stats.setEspecialidadMasComun(especialidadMasComun);
            
            Map<String, Integer> trabajadoresPorEspecialidad = new HashMap<>();
            especialidadCount.forEach((key, value) -> 
                trabajadoresPorEspecialidad.put(key, value.intValue()));
            stats.setTrabajadoresPorEspecialidad(trabajadoresPorEspecialidad);
        } else {
            stats.setEspecialidadMasComun("N/A");
            stats.setTrabajadoresPorEspecialidad(new HashMap<>());
        }
        
        return stats;
    }
}
