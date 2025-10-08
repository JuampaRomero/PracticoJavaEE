package PrestadorSalud.web.soap;

import PrestadorSalud.messaging.PrestadorAltaProducer;
import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.service.PrestadorSaludServiceLocal;
import PrestadorSalud.web.soap.dto.PrestadorSaludSoapDto;
import PrestadorSalud.web.soap.dto.PrestadorSaludSoapRequest;
import jakarta.ejb.EJB;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceException;

import java.util.List;
import java.util.stream.Collectors;

@WebService(serviceName = "PrestadorSaludService")
public class PrestadorSaludSoapEndpoint {

    @EJB
    private PrestadorSaludServiceLocal service;

    @EJB
    private PrestadorAltaProducer altaProducer;

    @WebMethod
    public List<PrestadorSaludSoapDto> listarPrestadores() {
        return service.obtenerPrestadores().stream()
                .map(PrestadorSaludSoapDto::fromEntity)
                .collect(Collectors.toList());
    }

    @WebMethod
    public List<PrestadorSaludSoapDto> buscarPorNombre(@WebParam(name = "nombre") String nombre) {
        return service.buscarPorNombre(nombre).stream()
                .map(PrestadorSaludSoapDto::fromEntity)
                .collect(Collectors.toList());
    }

    @WebMethod
    public String altaPrestador(@WebParam(name = "prestador") PrestadorSaludSoapRequest request) {
        try {
            PrestadorSalud prestador = request.toEntity();
            altaProducer.enviarAlta(prestador);
            return "El alta se está procesando";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new WebServiceException(ex.getMessage(), ex);
        }
    }
}
