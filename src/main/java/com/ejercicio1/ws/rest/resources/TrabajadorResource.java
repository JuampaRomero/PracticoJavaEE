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
       // Validar que la cédula no sea null o vacía
       if (!cedula.isEmpty()) {
           // Llamar a trabajadorService.buscarPorCedula(cedula)
           TrabajadorSalud trabajadorPorCedula = trabajadorService.buscarPorCedula(cedula);
           if (trabajadorPorCedula != null) {
               // Si se encuentra, convertir a DTO y retornar en ResponseWrapper
               TrabajadorDTO trabajadorDTO = TrabajadorMapper.toDTO(trabajadorPorCedula);
               ResponseWrapper<TrabajadorDTO> responseWrapper =
                       ResponseWrapper.success(trabajadorDTO, "Trabajador encontrado exitosamente");

               return Response.ok(responseWrapper).build(); // Fixed: Added return statement
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

    

    // PUT /trabajadores/{cedula} Actualiza un trabajador existente

    @PUT
    @Path("/{cedula}")
    public Response actualizar(@PathParam("cedula") String cedula, TrabajadorDTO trabajadorDTO) {
        // TODO: Implementar la actualización

        // 1. Validar parámetros
        if(!cedula.isEmpty()){
            // 2. Verificar que el trabajador existe (buscarPorCedula)
            TrabajadorSalud tds = trabajadorService.buscarPorCedula(cedula);
            if(!tds.equals(null)){
                try {
                    // 3. Actualizar los campos (excepto la cédula)
                    tds.setNombre(trabajadorDTO.getNombre());
                    tds.setApellido(trabajadorDTO.getApellido());
                    tds.setEspecialidad(trabajadorDTO.getEspecialidad());
                    tds.setMatriculaProfesional(trabajadorDTO.getMatriculaProfesional());
                    tds.setFechaIngreso(trabajadorDTO.getFechaIngreso());
                    tds.setActivo(trabajadorDTO.isActivo());
                    TrabajadorDTO trabajadorActualizadoDTO = TrabajadorMapper.toDTO(tds);
                    ResponseWrapper<TrabajadorDTO> responseWrapper =
                            ResponseWrapper.success(trabajadorActualizadoDTO, "Trabajador actualizado exitosamente");
                    return Response.ok(responseWrapper).build();
                } catch (RuntimeException e) {
                    ResponseWrapper<TrabajadorDTO> responseWrapper =
                            ResponseWrapper.error("Fallo al actualizar el Trabajador: " + e.getMessage());
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(responseWrapper)
                            .build();
                } catch(Exception e){throw new RuntimeException(e);}
            }else{
                ResponseWrapper<TrabajadorDTO> responseWrapper =
                        ResponseWrapper.error("Trabajador no encontrado con cédula: " + cedula);

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseWrapper)
                        .build();
            }
        }else{
            ResponseWrapper<TrabajadorDTO> responseWrapper =
                    ResponseWrapper.error("La cédula no puede ser nula o vacía");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseWrapper)
                    .build();
        }

    }
    
    /**
     * DELETE /trabajadores/{cedula}
     * Elimina (desactiva) un trabajador
     */
    @DELETE
    @Path("/{cedula}")
    public Response eliminar(@PathParam("cedula") String cedula) {
        if(!cedula.isEmpty()){
            TrabajadorSalud tds = trabajadorService.buscarPorCedula(cedula);
            if(!tds.equals(null)) {
                try {
                    trabajadorService.eliminarTrabajador(cedula);
                    return Response.noContent().build();

                } catch (BusinessException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                ResponseWrapper<TrabajadorDTO> responseWrapper =
                        ResponseWrapper.error("Trabajador no encontrado con cédula: " + cedula);

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseWrapper)
                        .build();

            }
        }else{
            ResponseWrapper<TrabajadorDTO> responseWrapper =
                    ResponseWrapper.error("La cédula no puede ser nula o vacía");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseWrapper)
                    .build();
        }
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
        if(!especialidad.isEmpty()){
            List<TrabajadorSalud> trabajadoresPorEspecialidad = trabajadorService.buscarPorEspecialidad(especialidad);
            if(!trabajadoresPorEspecialidad.equals(null)){
                List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadoresPorEspecialidad);
                ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                        ResponseWrapper.success(trabajadoresDTOs, "Trabajadores con especialidad " + especialidad + " encontrados exitosamente");
                return Response.ok(responseWrapper).build();
            }else{
                ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                        ResponseWrapper.error("No se encontraron trabajadores con la especialidad: " + especialidad);

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseWrapper)
                        .build();
            }
        }else{
            ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                    ResponseWrapper.error("La especialidad no puede ser nula o vacía");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(responseWrapper)
                    .build();
        }
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
        try {
            List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
            List<TrabajadorSalud> trabajadoresActivos = trabajadores.stream()
                    .filter(TrabajadorSalud::isActivo)
                    .toList();
            if(!trabajadoresActivos.isEmpty()){
                List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadoresActivos);
                ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                        ResponseWrapper.success(trabajadoresDTOs, "Trabajadores activos obtenidos exitosamente");
                return Response.ok(responseWrapper).build();
            }else{
                ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                        ResponseWrapper.error("No se encontraron trabajadores activos");

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseWrapper)
                        .build();
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    
    /**
     * GET /trabajadores/buscar
     * Búsqueda con query parameters
     * Ejemplo: /trabajadores/buscar?nombre=Juan&activo=true
     */
    @GET
    @Path("/buscar")
    public Response buscar(
            //Esto despues tenemos que cambiarlo por parametros que esten indexados en la BD, o indexar por estos supongo..
            @QueryParam("nombre") String nombre,
            @QueryParam("apellido") String apellido,
            @QueryParam("especialidad") String especialidad,
            @QueryParam("activo") Boolean activo) {
        // TODO: Implementar búsqueda con filtros múltiples

        try {
            List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
            List<TrabajadorSalud> trabajadoresPorFiltro = trabajadores.stream()
                    .filter(t -> (nombre == null || t.getNombre().equalsIgnoreCase(nombre)) &&
                            (apellido == null || t.getApellido().equalsIgnoreCase(apellido)) &&
                            (especialidad == null || t.getEspecialidad().equalsIgnoreCase(especialidad)) &&
                            (activo == null || t.isActivo() == activo))
                    .toList();
            if(trabajadoresPorFiltro.stream().count() > 0) {
                List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadoresPorFiltro);
                ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                        ResponseWrapper.success(trabajadoresDTOs, "Trabajadores encontrados con los filtros proporcionados");
                return Response.ok(responseWrapper).build();
            }else{
                ResponseWrapper<List<TrabajadorDTO>> responseWrapper =
                        ResponseWrapper.error("No se encontraron trabajadores con los filtros proporcionados");

                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseWrapper)
                        .build();
            }
        } catch (RuntimeException e) {throw new RuntimeException(e);
        } catch (Exception e) {throw new RuntimeException(e);}}
    }
}