package com.ejercicio1.dao;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class TrabajadorSaludDAO implements TrabajadorSaludDAOLocal, TrabajadorSaludDAORemote {
    
    private static final Logger LOGGER = Logger.getLogger(TrabajadorSaludDAO.class.getName());
    
    // Lista thread-safe para almacenar los trabajadores
    private List<TrabajadorSalud> trabajadores;
    @PostConstruct
    public void init() {
        trabajadores = new CopyOnWriteArrayList<>();
        LOGGER.info("TrabajadorSaludDAO inicializado correctamente");
    }
    
    @Override
    @Lock(LockType.WRITE)
    public void agregar(TrabajadorSalud trabajador) {
        if (trabajador == null) {
            throw new IllegalArgumentException("El trabajador no puede ser null");
        }
        
        if (existeCedula(trabajador.getCedula())) {
            throw new IllegalArgumentException("Ya existe un trabajador con la cédula: " + trabajador.getCedula());
        }
        
        if (existeMatricula(trabajador.getMatriculaProfesional())) {
            throw new IllegalArgumentException("Ya existe un trabajador con la matrícula: " + trabajador.getMatriculaProfesional());
        }
        
        trabajadores.add(trabajador);
        LOGGER.info("Trabajador agregado: " + trabajador.getCedula() + " - " + trabajador.getNombre() + " " + trabajador.getApellido());
    }
    
    @Override
    @Lock(LockType.READ)
    public List<TrabajadorSalud> obtenerTodos() {
        return new ArrayList<>(trabajadores);
    }
    
    @Override
    @Lock(LockType.READ)
    public TrabajadorSalud buscarPorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        
        return trabajadores.stream()
                .filter(t -> t.getCedula().equals(cedula.trim()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    @Lock(LockType.READ)
    public boolean existeCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }
        
        return trabajadores.stream()
                .anyMatch(t -> t.getCedula().equals(cedula.trim()));
    }
    
    @Override
    @Lock(LockType.READ)
    public boolean existeMatricula(Integer matricula) {
        if (matricula == null) {
            return false;
        }
        
        return trabajadores.stream()
                .anyMatch(t -> t.getMatriculaProfesional().equals(matricula));
    }
    
    @Override
    @Lock(LockType.READ)
    public int contarTrabajadores() {
        return trabajadores.size();
    }
    
    @Override
    @Lock(LockType.READ)
    public int contarTrabajadoresActivos() {
        return (int) trabajadores.stream()
                .filter(TrabajadorSalud::isActivo)
                .count();
    }
    
    @Override
    @Lock(LockType.WRITE)
    public void eliminar(TrabajadorSalud trabajador) {
        if (trabajador == null) {
            throw new IllegalArgumentException("El trabajador no puede ser null");
        }
        
        boolean eliminado = trabajadores.removeIf(t -> t.getCedula().equals(trabajador.getCedula()));
        
        if (eliminado) {
            LOGGER.info("Trabajador eliminado: " + trabajador.getCedula() + " - " + trabajador.getNombre() + " " + trabajador.getApellido());
        } else {
            LOGGER.warning("No se pudo eliminar el trabajador con cédula: " + trabajador.getCedula());
        }
    }
    
    @Lock(LockType.WRITE)
    public void limpiarTodos() {
        trabajadores.clear();
        LOGGER.warning("Todos los trabajadores han sido eliminados del sistema");
    }
    
    @Lock(LockType.READ)
    public String obtenerEstadisticas() {
        int total = contarTrabajadores();
        int activos = contarTrabajadoresActivos();
        int inactivos = total - activos;
        
        return String.format("Total: %d, Activos: %d, Inactivos: %d", total, activos, inactivos);
    }
}
