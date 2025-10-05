package com.ejercicio1.ws.soap;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import com.ejercicio1.ws.rest.dto.TrabajadorDTO;
import com.ejercicio1.ws.rest.mappers.TrabajadorMapper;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Servicio SOAP para gestionar Trabajadores de Salud
 * 
 * Este servicio expone las mismas operaciones que TrabajadorResource pero usando SOAP
 * 
 * Configuración:
 * - @WebService: Define esta clase como un servicio web SOAP
 * - name: Nombre del servicio
 * - serviceName: Nombre del servicio en el WSDL
 * - targetNamespace: Espacio de nombres XML para el servicio
 * 
 * URL del WSDL estará disponible en: http://localhost:8080/GestorIdentidades/TrabajadorSoapService?wsdl
 */
@Stateless
@WebService(
    name = "TrabajadorSoapService",
    serviceName = "TrabajadorSoapService",
    targetNamespace = "http://soap.ws.ejercicio1.com/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class TrabajadorSoapService {

    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;

    /**
     * URL: http://localhost:8080/GestorIdentidades/TrabajadorSoapService?wsdl
     * Objetivo: Obtener todos los trabajadores
     */
    @WebMethod(operationName = "obtenerTodosTrabajadores")
    public List<TrabajadorDTO> obtenerTodos() {
        try {
            // 1. Obtener la lista de entidades desde el servicio de negocio
            List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
            
            // 2. Convertir las entidades a DTOs usando el mapper
            List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadores);
            
            // 3. Retornar la lista de DTOs
            // Nota: En SOAP no usamos ResponseWrapper, retornamos directamente los datos
            // Los errores se manejan mediante excepciones SOAP (SOAPFault)
            return trabajadoresDTOs;
            
        } catch (Exception e) {
            // 4. En SOAP, lanzamos una excepción que se convertirá en un SOAPFault
            throw new RuntimeException("Error al obtener trabajadores: " + e.getMessage());
        }
    }

    @WebMethod(operationName = "obtenerTrabajadorPorCI")
    public TrabajadorDTO obtenerTrabajadorPorCI(@WebParam(name = "cedula") String cedula) {
        if(cedula != null) {
            TrabajadorSalud tds = trabajadorService.buscarPorCedula(cedula);
            if(tds != null) {
                TrabajadorDTO trabajadorDTO = TrabajadorMapper.toDTO(tds);
                return trabajadorDTO;
            }else{
                throw new RuntimeException("Trabajador no encontrado");
            }
        }else{
            throw new RuntimeException("La cedula no puede ser nula");
        }
    }

    @WebMethod(operationName = "crearTrabajadorSoap")
    public TrabajadorDTO crearTrabajador(@WebParam(name = "trabajador") TrabajadorDTO trabajador) {
        if(trabajador != null) {
            try {
                TrabajadorSalud trabajadorEntity = TrabajadorMapper.toEntity(trabajador);
                trabajadorService.agregarTrabajador(trabajadorEntity);
                return trabajador;
            }catch(BusinessException e){
                throw new RuntimeException("Error de negocio:" + e.getMessage());
            }catch(Exception e) {
                throw new RuntimeException("Error al crear trabajador: " + e.getMessage());
            }
        }else{
            throw new RuntimeException("Trabajador no puede ser nulo");
        }
    }

    @WebMethod(operationName = "actualizarTrabajador")
    public TrabajadorDTO actualizarTrabajador(
            @WebParam(name = "trabajador") TrabajadorDTO trabajador,
            @WebParam(name= "cedula") String cedula){

        if(cedula != null && trabajador != null) {
            try {
                TrabajadorSalud tds = trabajadorService.buscarPorCedula(cedula);
                tds.setNombre(trabajador.getNombre());
                tds.setApellido(trabajador.getApellido());
                tds.setEspecialidad(trabajador.getEspecialidad());
                tds.setFechaIngreso(trabajador.getFechaIngreso());
                tds.setActivo(trabajador.isActivo());

                try {
                    TrabajadorDTO trabajadorActualizado = TrabajadorMapper.toDTO(tds);
                    return trabajadorActualizado;
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }catch(RuntimeException e){
                throw new RuntimeException("Error de negocio:" + e.getMessage());
            }
        }else{
            throw new RuntimeException("El trabajador y su CI no pueden ser nulos");
        }
    }

    @WebMethod(operationName = "eliminarTrabajador")
    public boolean eliminarTrabajador(@WebParam(name = "cedula") String cedula){
        if(cedula != null) {
            TrabajadorSalud tds =  trabajadorService.buscarPorCedula(cedula);
            if(tds != null) {
                try {
                    trabajadorService.eliminarTrabajador(cedula);
                    return true;
                }catch(BusinessException e){
                    throw new RuntimeException("Error de negocio:" + e.getMessage());
                }
            }else{
                throw new RuntimeException("Trabajador no encontrado con CI: " + cedula);
            }
        }else{
            throw new RuntimeException("El cedula no puede ser nula");
        }
    }

    @WebMethod(operationName = "buscarTrabajadoresPorEspecialidad")
    public List<TrabajadorDTO> buscarPorEspecialidad(@WebParam(name="especialidad") String especialidad){
        if(especialidad != null || !especialidad.isEmpty()) {
            List<TrabajadorSalud> trabajadores = trabajadorService.buscarPorEspecialidad(especialidad);
            if(trabajadores.stream().count() > 0){
                List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadores);
                return trabajadoresDTOs;
            }else{
                throw new RuntimeException("Trabajadores no encontrados con  especialidad: " + especialidad);
            }
        }else{
            throw new RuntimeException("El especialidad no puede ser nula o vacia");
        }
    }

    @WebMethod(operationName = "buscarTrabajadoresActivos")
    public List<TrabajadorDTO> buscarTrabajadoresActivos(){
        List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
        if((long) trabajadores.size() > 0) {
            trabajadores.stream()
                    .filter(TrabajadorSalud::isActivo)
                    .toList();
            if (trabajadores.stream().count() > 0) {
                List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadores);
                return trabajadoresDTOs;
            } else {
                throw new RuntimeException("No se encontraron trabajadores activos");
            }
        }else{
            throw new RuntimeException("No se encontraron trabajadores");
        }
    }

    @WebMethod(operationName = "buscarTrabajadoresPorFiltros")
    public List<TrabajadorDTO> buscarTrabajadoresPorFiltros( @WebParam(name = "nombre") String nombre,
                                                             @WebParam(name = "apellido") String apellido,
                                                             @WebParam(name = "especialidad") String especialidad,
                                                             @WebParam(name = "activo") Boolean activo){

        List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
        if(trabajadores.stream().count() > 0){
            List<TrabajadorSalud> trabajadoresFiltrados = trabajadores.stream()
            .filter(t -> (nombre == null || t.getNombre().equalsIgnoreCase(nombre)) &&
                         (apellido == null || t.getApellido().equalsIgnoreCase(apellido)) &&
                         (especialidad == null || t.getEspecialidad().equalsIgnoreCase(especialidad)) &&
                         (activo == null || t.isActivo() == activo))
                    .toList();

            List<TrabajadorDTO> dto = TrabajadorMapper.toDTOList(trabajadoresFiltrados);
            return dto;
        }else{
            throw new RuntimeException("No se encontraron trabajadores");
        }
    }

}
