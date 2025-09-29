package com.ejercicio1.ws.rest.resources;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import com.ejercicio1.ws.rest.dto.ResponseWrapper;
import com.ejercicio1.ws.rest.dto.TrabajadorDTO;
import com.ejercicio1.ws.rest.mappers.TrabajadorMapper;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * Recurso REST para gestionar Trabajadores de Salud
 * Base path: /api/v1/trabajadores
 */
@Path("/trabajadores")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrabajadorResource {

    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    //instanciar trabajador mapper
    private TrabajadorMapper trabajadorMapper = new TrabajadorMapper();

    //Retorno todos los trabajadores
    @GET
    public Response obtenerTodos() {
        try {
            List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
            List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadores);

            ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                    ResponseWrapper.success(trabajadoresDTOs, "Trabajadores obtenidos exitosamente");

            return Response.ok(responseWrapper).build();
        } catch (Exception e) {

            ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                    ResponseWrapper.error(e.getMessage());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(responseWrapper)
                    .build();
        }
    }

    
    /**
     * GET /trabajadores/{cedula}
     * Obtiene un trabajador por su cédula
     */
    @GET
    @Path("/{cedula}")
    public Response obtenerPorCedula(@PathParam("cedula") String cedula) {
        // TODO: Implementar la búsqueda por cédula
        // 1. Validar que la cédula no sea null o vacía
        if (!cedula.isEmpty()) {
            // 2. Llamar a trabajadorService.buscarPorCedula(cedula)
            TrabajadorSalud trabajadorPorCedula = trabajadorService.buscarPorCedula(cedula);
            if (!trabajadorPorCedula.equals(null)) {
                // 3. Si se encuentra, convertir a DTO y retornar en ResponseWrapper
                TrabajadorDTO trabajadorDTO = TrabajadorMapper.toDTO(trabajadorPorCedula);
                ResponseWrapper<TrabajadorDTO> responseWrapper =
                        ResponseWrapper.success(trabajadorDTO, "Trabajador encontrado exitosamente");
            } else {
                ResponseWrapper<TrabajadorDTO> responseWrapper =
                        ResponseWrapper.error("Trabajador no encontrado con cédula: " + cedula);

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseWrapper)
                        .build();
            }
        } else {
            ResponseWrapper<TrabajadorDTO> responseWrapper =
                    ResponseWrapper.error("La cédula no puede ser nula o vacía");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseWrapper)
                    .build();
            {

            }
        }
    }
    
    /**
     * POST /trabajadores
     * Crea un nuevo trabajador
     */
    @POST
    public Response crear(TrabajadorDTO trabajadorDTO) {
        // TODO: Implementar la creación de trabajador
        if (trabajadorDTO != null) {
            TrabajadorSalud trabajadorEntity = TrabajadorMapper.toEntity(trabajadorDTO);
            try {
                trabajadorService.agregarTrabajador(trabajadorEntity);
                ResponseWrapper<TrabajadorDTO> responseWrapper =
                        ResponseWrapper.success(trabajadorDTO, "Trabajador creado exitosamente");
                return Response.status(Response.Status.CREATED).build();
            } catch (BusinessException e) {
                ResponseWrapper<TrabajadorDTO> responseWrapper =
                        ResponseWrapper.error("Fallo al crear el Trabajador: " + e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(responseWrapper)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }else{
            ResponseWrapper<TrabajadorDTO> responseWrapper =
                    ResponseWrapper.error("El Trabajador no puede ser nulo");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseWrapper)
                    .build();
        }
    }

    
    /**
     * PUT /trabajadores/{cedula}
     * Actualiza un trabajador existente
     */
    @PUT
    @Path("/{cedula}")
    public Response actualizar(@PathParam("cedula") String cedula, TrabajadorDTO trabajadorDTO) {
        // TODO: Implementar la actualización
        // 1. Validar parámetros
        // 2. Verificar que el trabajador existe (buscarPorCedula)
        // 3. Actualizar los campos (excepto la cédula)
        // 4. Guardar cambios
        // 5. Retornar el trabajador actualizado
        // Nota: El servicio actual no tiene método update, considerar agregarlo o usar workaround
        throw new UnsupportedOperationException("Método no implementado");
    }
    
    /**
     * DELETE /trabajadores/{cedula}
     * Elimina (desactiva) un trabajador
     */
    @DELETE
    @Path("/{cedula}")
    public Response eliminar(@PathParam("cedula") String cedula) {
        // TODO: Implementar la eliminación lógica
        // 1. Buscar el trabajador por cédula
        // 2. Si no existe, retornar 404
        // 3. Cambiar el estado a inactivo (soft delete)
        // 4. Guardar cambios
        // 5. Retornar 204 (No Content) o 200 con mensaje de confirmación
        throw new UnsupportedOperationException("Método no implementado");
    }
    
    /**
     * GET /trabajadores/especialidad/{especialidad}
     * Busca trabajadores por especialidad
     */
    @GET
    @Path("/especialidad/{especialidad}")
    public Response buscarPorEspecialidad(@PathParam("especialidad") String especialidad) {
        // TODO: Implementar búsqueda por especialidad
        // 1. Validar parámetro
        // 2. Llamar a trabajadorService.buscarPorEspecialidad()
        // 3. Convertir resultados a DTOs
        // 4. Retornar lista envuelta en ResponseWrapper
        throw new UnsupportedOperationException("Método no implementado");
    }
    
    /**
     * GET /trabajadores/activos
     * Obtiene solo los trabajadores activos
     */
    @GET
    @Path("/activos")
    public Response obtenerActivos() {
        // TODO: Implementar filtrado de trabajadores activos
        // 1. Obtener todos los trabajadores
        // 2. Filtrar solo los activos
        // 3. Convertir a DTOs y retornar
        throw new UnsupportedOperationException("Método no implementado");
    }
    
    /**
     * GET /trabajadores/buscar
     * Búsqueda con query parameters
     * Ejemplo: /trabajadores/buscar?nombre=Juan&activo=true
     */
    @GET
    @Path("/buscar")
    public Response buscar(
            @QueryParam("nombre") String nombre,
            @QueryParam("apellido") String apellido,
            @QueryParam("especialidad") String especialidad,
            @QueryParam("activo") Boolean activo) {
        // TODO: Implementar búsqueda con filtros múltiples
        // 1. Obtener todos los trabajadores
        // 2. Aplicar filtros según los parámetros recibidos
        // 3. Retornar resultados filtrados
        throw new UnsupportedOperationException("Método no implementado");
    }
}