package com.ejercicio1.ws.soap;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import com.ejercicio1.ws.rest.dto.TrabajadorDTO;
import com.ejercicio1.ws.rest.mappers.TrabajadorMapper;

import jakarta.ejb.EJB;
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

    // TODO: Implementar obtenerPorCedula siguiendo los pasos anteriores
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

    // TODO: Implementar crear siguiendo los pasos anteriores
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
    /**
     * ==================================================================================
     * MÉTODO 6: buscarPorEspecialidad
     * ==================================================================================
     * Busca trabajadores por especialidad
     * 
     * PASOS A SEGUIR:
     * 
     * 1. Agregar la anotación @WebMethod
     *    @WebMethod(operationName = "buscarTrabajadoresPorEspecialidad")
     * 
     * 2. Definir el método que retorne List<TrabajadorDTO>
     *    public List<TrabajadorDTO> buscarPorEspecialidad(...)
     * 
     * 3. Agregar el parámetro String especialidad con @WebParam
     *    @WebParam(name = "especialidad") String especialidad
     * 
     * 4. Validar que la especialidad no sea null o vacía
     *    if (especialidad == null || especialidad.isEmpty()) {
     *        throw new RuntimeException("La especialidad no puede ser nula o vacía");
     *    }
     * 
     * 5. Llamar al servicio de negocio para buscar por especialidad
     *    List<TrabajadorSalud> trabajadores = trabajadorService.buscarPorEspecialidad(especialidad);
     * 
     * 6. Verificar si se encontraron trabajadores (opcional)
     *    if (trabajadores == null || trabajadores.isEmpty()) {
     *        // Puedes retornar lista vacía o lanzar excepción según el diseño
     *        return new ArrayList<>();
     *    }
     * 
     * 7. Convertir las entidades a DTOs
     *    List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadores);
     * 
     * 8. Retornar la lista de DTOs
     *    return trabajadoresDTOs;
     * 
     * 9. Envolver en try-catch para manejar excepciones
     */
    // TODO: Implementar buscarPorEspecialidad siguiendo los pasos anteriores

    /**
     * ==================================================================================
     * MÉTODO 7: obtenerActivos
     * ==================================================================================
     * Obtiene solo los trabajadores activos
     * 
     * PASOS A SEGUIR:
     * 
     * 1. Agregar la anotación @WebMethod
     *    @WebMethod(operationName = "obtenerTrabajadoresActivos")
     * 
     * 2. Definir el método que retorne List<TrabajadorDTO>
     *    public List<TrabajadorDTO> obtenerActivos()
     * 
     * 3. Obtener todos los trabajadores desde el servicio de negocio
     *    List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
     * 
     * 4. Filtrar solo los trabajadores activos usando streams
     *    List<TrabajadorSalud> trabajadoresActivos = trabajadores.stream()
     *        .filter(TrabajadorSalud::isActivo)
     *        .toList();
     * 
     * 5. Verificar si hay trabajadores activos (opcional)
     *    if (trabajadoresActivos.isEmpty()) {
     *        return new ArrayList<>();
     *    }
     * 
     * 6. Convertir las entidades a DTOs
     *    List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadoresActivos);
     * 
     * 7. Retornar la lista de DTOs
     *    return trabajadoresDTOs;
     * 
     * 8. Envolver en try-catch para manejar excepciones
     */
    // TODO: Implementar obtenerActivos siguiendo los pasos anteriores

    /**
     * ==================================================================================
     * MÉTODO 8: buscarConFiltros
     * ==================================================================================
     * Búsqueda con múltiples filtros opcionales
     * 
     * PASOS A SEGUIR:
     * 
     * 1. Agregar la anotación @WebMethod
     *    @WebMethod(operationName = "buscarTrabajadoresConFiltros")
     * 
     * 2. Definir el método que retorne List<TrabajadorDTO>
     *    public List<TrabajadorDTO> buscarConFiltros(...)
     * 
     * 3. Agregar múltiples parámetros opcionales con @WebParam:
     *    @WebParam(name = "nombre") String nombre,
     *    @WebParam(name = "apellido") String apellido,
     *    @WebParam(name = "especialidad") String especialidad,
     *    @WebParam(name = "activo") Boolean activo
     * 
     * 4. Obtener todos los trabajadores desde el servicio de negocio
     *    List<TrabajadorSalud> trabajadores = trabajadorService.obtenerTodos();
     * 
     * 5. Aplicar filtros usando streams (similar al método buscar de REST):
     *    List<TrabajadorSalud> trabajadoresFiltrados = trabajadores.stream()
     *        .filter(t -> (nombre == null || t.getNombre().equalsIgnoreCase(nombre)) &&
     *                     (apellido == null || t.getApellido().equalsIgnoreCase(apellido)) &&
     *                     (especialidad == null || t.getEspecialidad().equalsIgnoreCase(especialidad)) &&
     *                     (activo == null || t.isActivo() == activo))
     *        .toList();
     * 
     * 6. Convertir las entidades filtradas a DTOs
     *    List<TrabajadorDTO> trabajadoresDTOs = TrabajadorMapper.toDTOList(trabajadoresFiltrados);
     * 
     * 7. Retornar la lista de DTOs (puede estar vacía)
     *    return trabajadoresDTOs;
     * 
     * 8. Envolver en try-catch para manejar excepciones
     */
    // TODO: Implementar buscarConFiltros siguiendo los pasos anteriores

    /**
     * ==================================================================================
     * NOTAS IMPORTANTES SOBRE SERVICIOS SOAP
     * ==================================================================================
     * 
     * 1. DIFERENCIAS CON REST:
     *    - No se usa ResponseWrapper, se retornan los datos directamente
     *    - Los errores se manejan con excepciones que se convierten en SOAPFault
     *    - No hay códigos de estado HTTP (200, 404, 500), todo es mediante excepciones
     * 
     * 2. ANOTACIONES PRINCIPALES:
     *    - @WebService: Define la clase como servicio SOAP
     *    - @WebMethod: Define un método como operación del servicio
     *    - @WebParam: Define los parámetros de entrada con nombres explícitos
     *    - @SOAPBinding: Define el estilo del servicio (DOCUMENT o RPC)
     * 
     * 3. TIPOS DE DATOS:
     *    - Tipos simples (String, int, boolean, etc.) se mapean directamente
     *    - Objetos complejos (DTOs) deben ser serializables a XML
     *    - Las listas se convierten en arrays XML
     * 
     * 4. MANEJO DE ERRORES:
     *    - Usar RuntimeException para errores generales
     *    - O crear una clase personalizada anotada con @WebFault
     *    - El servidor SOAP convertirá las excepciones en elementos <soap:Fault>
     * 
     * 5. PRUEBAS:
     *    - Acceder al WSDL: http://localhost:8080/GestorIdentidades/TrabajadorSoapService?wsdl
     *    - Usar herramientas como SoapUI o Postman para probar los servicios
     *    - El WSDL describe todos los métodos disponibles y sus parámetros
     * 
     * 6. DEPLOYMENT:
     *    - Los servicios SOAP se exponen automáticamente en el servidor de aplicaciones
     *    - No necesitas configuración adicional como con REST (@ApplicationPath)
     *    - La URL base es: http://<host>:<port>/<context-root>/<service-name>
     */
}
