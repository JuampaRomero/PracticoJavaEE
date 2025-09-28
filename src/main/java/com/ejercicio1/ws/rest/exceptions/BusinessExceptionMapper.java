package com.ejercicio1.ws.rest.exceptions;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.ws.rest.dto.ResponseWrapper;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapper para manejar BusinessException y convertirlas en respuestas HTTP apropiadas
 */
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {
    
    @Override
    public Response toResponse(BusinessException exception) {
        // Crear un ResponseWrapper con el error
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(exception.getMessage());
        
        // Retornar Response con status 400 (Bad Request)
        // 400 es apropiado para errores de validación de negocio
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorResponse)
            .build();
    }
}
