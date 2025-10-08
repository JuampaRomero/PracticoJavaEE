package PrestadorSalud.web.rest;

import PrestadorSalud.messaging.PrestadorAltaProducer;
import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;
import PrestadorSalud.service.PrestadorSaludServiceLocal;
import PrestadorSalud.web.rest.dto.PrestadorSaludRequest;
import PrestadorSalud.web.rest.dto.PrestadorSaludResponse;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("/prestadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrestadorSaludResource {

    @EJB
    private PrestadorSaludServiceLocal service;

    @EJB
    private PrestadorAltaProducer altaProducer;

    @GET
    public List<PrestadorSaludResponse> listar(@QueryParam("nombre") String nombre) {
        List<PrestadorSalud> prestadores = (nombre != null && !nombre.isBlank())
                ? service.buscarPorNombre(nombre)
                : service.obtenerPrestadores();
        return prestadores.stream()
                .map(PrestadorSaludResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @POST
    public Response crear(PrestadorSaludRequest request) {
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El cuerpo de la solicitud es obligatorio")
                    .build();
        }
        try {
            PrestadorSalud prestador = new PrestadorSalud();
            prestador.setNombre(request.getNombre());
            prestador.setDireccion(request.getDireccion());
            prestador.setTipo(parseTipo(request.getTipo()));
            altaProducer.enviarAlta(prestador);
            return Response.accepted()
                    .location(URI.create("/api/prestadores"))
                    .entity("El alta se está procesando")
                    .build();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    private TipoPrestador parseTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        try {
            return TipoPrestador.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de prestador desconocido: " + tipo);
        }
    }
}
