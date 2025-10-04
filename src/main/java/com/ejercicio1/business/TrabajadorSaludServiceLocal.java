package com.ejercicio1.business;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.Local;
import java.util.List;

/**
 * Interfaz local para el servicio de negocio de TrabajadorSalud.
 * Define las operaciones de negocio disponibles localmente.
 */
@Local
public interface TrabajadorSaludServiceLocal {
    
    /**
     * Agrega un nuevo trabajador de salud aplicando las reglas de negocio.
     * 
     * @param trabajador El trabajador a agregar
     * @throws BusinessException si no se cumplen las reglas de negocio
     */
    void agregarTrabajador(TrabajadorSalud trabajador) throws BusinessException;
    
    /**
     * Obtiene todos los trabajadores de salud.
     * 
     * @return Lista de todos los trabajadores
     */
    List<TrabajadorSalud> obtenerTodos();
    
    /**
     * Busca trabajadores por su especialidad.
     * 
     * @param especialidad La especialidad a buscar
     * @return Lista de trabajadores con esa especialidad
     */
    List<TrabajadorSalud> buscarPorEspecialidad(String especialidad);
    
    /**
     * Busca un trabajador por su cédula.
     * 
     * @param cedula La cédula del trabajador
     * @return El trabajador encontrado o null si no existe
     */
    TrabajadorSalud buscarPorCedula(String cedula);
    
    /**
     * Obtiene estadísticas del sistema.
     * 
     * @return Objeto con las estadísticas del sistema
     */
    EstadisticasSistema obtenerEstadisticas();
    
    /**
     * Valida si un trabajador cumple con todas las reglas de negocio.
     * 
     * @param trabajador El trabajador a validar
     * @throws BusinessException si no cumple alguna regla
     */
    void validarTrabajador(TrabajadorSalud trabajador) throws BusinessException;

    void eliminarTrabajador(String cedula) throws BusinessException;
}
