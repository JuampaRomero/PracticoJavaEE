package com.ejercicio1.business;

/**
 * Excepción personalizada para manejar errores de negocio.
 */
public class BusinessException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
