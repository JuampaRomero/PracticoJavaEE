package PrestadorSalud.web.rest.dto;

import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;

import java.time.LocalDateTime;

public class PrestadorSaludResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private TipoPrestador tipo;
    private LocalDateTime fechaRegistro;

    public PrestadorSaludResponse() {
    }

    private PrestadorSaludResponse(Long id, String nombre, String direccion, TipoPrestador tipo, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipo = tipo;
        this.fechaRegistro = fechaRegistro;
    }

    public static PrestadorSaludResponse fromEntity(PrestadorSalud prestador) {
        return new PrestadorSaludResponse(
                prestador.getId(),
                prestador.getNombre(),
                prestador.getDireccion(),
                prestador.getTipo(),
                prestador.getRegDate()
        );
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public TipoPrestador getTipo() {
        return tipo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
