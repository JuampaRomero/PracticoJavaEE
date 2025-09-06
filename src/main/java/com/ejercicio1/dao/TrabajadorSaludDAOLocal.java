package com.ejercicio1.dao;

import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.ejb.Local;
import java.util.List;

/**
 * Interfaz local para el acceso a datos de TrabajadorSalud.
 * Esta interfaz define las operaciones de alta y consulta requeridas.
 */
@Local
public interface TrabajadorSaludDAOLocal {
    
    /**
     * Agrega un nuevo trabajador de salud al sistema.
     * 
     * @param trabajador El trabajador a agregar
     * @throws IllegalArgumentException si el trabajador es null o ya existe
     */
    void agregar(TrabajadorSalud trabajador);
    
    /**
     * Obtiene todos los trabajadores de salud registrados.
     * 
     * @return Lista de todos los trabajadores
     */
    List<TrabajadorSalud> obtenerTodos();
    
    /**
     * Busca un trabajador por su cédula.
     * 
     * @param cedula La cédula del trabajador
     * @return El trabajador encontrado o null si no existe
     */
    TrabajadorSalud buscarPorCedula(String cedula);
    
    /**
     * Verifica si existe un trabajador con la cédula especificada.
     * 
     * @param cedula La cédula a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeCedula(String cedula);
    
    /**
     * Verifica si existe un trabajador con la matrícula especificada.
     * 
     * @param matricula La matrícula a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeMatricula(Integer matricula);
    
    /**
     * Obtiene la cantidad total de trabajadores registrados.
     * 
     * @return El número total de trabajadores
     */
    int contarTrabajadores();
    
    /**
     * Obtiene la cantidad de trabajadores activos.
     * 
     * @return El número de trabajadores activos
     */
    int contarTrabajadoresActivos();
}
