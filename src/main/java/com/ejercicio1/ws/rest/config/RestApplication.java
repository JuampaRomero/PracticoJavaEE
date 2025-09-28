package com.ejercicio1.ws.rest.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuración de la aplicación JAX-RS
 * Define el path base para todos los servicios REST
 */
@ApplicationPath("/api/v1")
public class RestApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // Registrar aquí las clases de recursos REST
        resources.add(com.ejercicio1.ws.rest.resources.TrabajadorResource.class);
        
        // Registrar solo el mapper de BusinessException (opcional pero útil)
        resources.add(com.ejercicio1.ws.rest.exceptions.BusinessExceptionMapper.class);
        
        return resources;
    }
}