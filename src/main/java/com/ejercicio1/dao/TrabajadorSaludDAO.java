package com.ejercicio1.dao;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Logger;

/**
 * DAO para TrabajadorSalud usando JPA y PostgreSQL
 * Reemplaza la implementación en memoria por persistencia real en base de datos
 */
@Stateless
public class TrabajadorSaludDAO implements TrabajadorSaludDAOLocal, TrabajadorSaludDAORemote {
    
    private static final Logger LOGGER = Logger.getLogger(TrabajadorSaludDAO.class.getName());
    
    @PersistenceContext(unitName = "GestorIdentidadesPU")
    private EntityManager entityManager;
    
    @Override
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
        
        entityManager.persist(trabajador);
        LOGGER.info("Trabajador persistido en BD: " + trabajador.getCedula() + " - " + trabajador.getNombre() + " " + trabajador.getApellido());
    }
    
    @Override
    public List<TrabajadorSalud> obtenerTodos() {
        TypedQuery<TrabajadorSalud> query = entityManager.createQuery(
            "SELECT t FROM TrabajadorSalud t", TrabajadorSalud.class);
        return query.getResultList();
    }
    
    @Override
    public TrabajadorSalud buscarPorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        
        return entityManager.find(TrabajadorSalud.class, cedula.trim());
    }
    
    @Override
    public boolean existeCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }
        
        return buscarPorCedula(cedula) != null;
    }
    
    @Override
    public boolean existeMatricula(Integer matricula) {
        if (matricula == null) {
            return false;
        }
        
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM TrabajadorSalud t WHERE t.matriculaProfesional = :matricula", Long.class);
        query.setParameter("matricula", matricula);
        return query.getSingleResult() > 0;
    }
    
    @Override
    public int contarTrabajadores() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM TrabajadorSalud t", Long.class);
        return query.getSingleResult().intValue();
    }
    
    @Override
    public int contarTrabajadoresActivos() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(t) FROM TrabajadorSalud t WHERE t.activo = true", Long.class);
        return query.getSingleResult().intValue();
    }
    
    @Override
    public void eliminar(TrabajadorSalud trabajador) {
        if (trabajador == null) {
            throw new IllegalArgumentException("El trabajador no puede ser null");
        }
        
        TrabajadorSalud trabajadorEnBD = entityManager.find(TrabajadorSalud.class, trabajador.getCedula());
        
        if (trabajadorEnBD != null) {
            entityManager.remove(trabajadorEnBD);
            LOGGER.info("Trabajador eliminado de BD: " + trabajador.getCedula() + " - " + trabajador.getNombre() + " " + trabajador.getApellido());
        } else {
            LOGGER.warning("No se pudo eliminar el trabajador con cédula: " + trabajador.getCedula());
        }
    }
    
    public void limpiarTodos() {
        entityManager.createQuery("DELETE FROM TrabajadorSalud").executeUpdate();
        LOGGER.warning("Todos los trabajadores han sido eliminados de la base de datos");
    }
    
    public String obtenerEstadisticas() {
        int total = contarTrabajadores();
        int activos = contarTrabajadoresActivos();
        int inactivos = total - activos;
        
        return String.format("Total: %d, Activos: %d, Inactivos: %d", total, activos, inactivos);
    }
}
