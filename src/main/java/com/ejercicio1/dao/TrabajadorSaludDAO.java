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

/**
 * Implementación Singleton Session Bean para el acceso a datos de TrabajadorSalud.
 * Este bean gestiona el almacenamiento en memoria de los trabajadores de salud.
 * Al ser un Singleton, garantiza una única instancia en toda la aplicación.
 */
@Singleton
@Startup // Se inicializa al arrancar la aplicación
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER) // El contenedor gestiona la concurrencia
public class TrabajadorSaludDAO implements TrabajadorSaludDAOLocal, TrabajadorSaludDAORemote {
    
    private static final Logger LOGGER = Logger.getLogger(TrabajadorSaludDAO.class.getName());
    
    // Lista thread-safe para almacenar los trabajadores
    private List<TrabajadorSalud> trabajadores;

    //Hola soy un commit nuevo !
    /**
     * Inicializa el DAO al arrancar la aplicación.
     */
    @PostConstruct
    public void init() {
        trabajadores = new CopyOnWriteArrayList<>();
        LOGGER.info("TrabajadorSaludDAO inicializado correctamente");
    }
    
    /**
     * Agrega un nuevo trabajador de salud al sistema.
     * Utiliza un lock de escritura para garantizar consistencia.
     */
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
    
    /**
     * Obtiene todos los trabajadores de salud registrados.
     * Utiliza un lock de lectura para permitir acceso concurrente.
     */
    @Override
    @Lock(LockType.READ)
    public List<TrabajadorSalud> obtenerTodos() {
        // Retorna una copia para evitar modificaciones externas
        return new ArrayList<>(trabajadores);
    }
    
    /**
     * Busca un trabajador por su cédula.
     */
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
    
    /**
     * Verifica si existe un trabajador con la cédula especificada.
     */
    @Override
    @Lock(LockType.READ)
    public boolean existeCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }
        
        return trabajadores.stream()
                .anyMatch(t -> t.getCedula().equals(cedula.trim()));
    }
    
    /**
     * Verifica si existe un trabajador con la matrícula especificada.
     */
    @Override
    @Lock(LockType.READ)
    public boolean existeMatricula(Integer matricula) {
        if (matricula == null) {
            return false;
        }
        
        return trabajadores.stream()
                .anyMatch(t -> t.getMatriculaProfesional().equals(matricula));
    }
    
    /**
     * Obtiene la cantidad total de trabajadores registrados.
     */
    @Override
    @Lock(LockType.READ)
    public int contarTrabajadores() {
        return trabajadores.size();
    }
    
    /**
     * Obtiene la cantidad de trabajadores activos.
     */
    @Override
    @Lock(LockType.READ)
    public int contarTrabajadoresActivos() {
        return (int) trabajadores.stream()
                .filter(TrabajadorSalud::isActivo)
                .count();
    }
    
    /**
     * Método útil para limpiar todos los datos (útil para pruebas).
     * Requiere permisos especiales para ejecutarse.
     */
    @Lock(LockType.WRITE)
    public void limpiarTodos() {
        trabajadores.clear();
        LOGGER.warning("Todos los trabajadores han sido eliminados del sistema");
    }
    
    /**
     * Obtiene estadísticas del sistema.
     */
    @Lock(LockType.READ)
    public String obtenerEstadisticas() {
        int total = contarTrabajadores();
        int activos = contarTrabajadoresActivos();
        int inactivos = total - activos;
        
        return String.format("Total: %d, Activos: %d, Inactivos: %d", total, activos, inactivos);
    }
}
