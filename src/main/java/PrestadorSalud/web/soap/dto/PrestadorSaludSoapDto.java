package PrestadorSalud.web.soap.dto;

import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import java.time.format.DateTimeFormatter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrestadorSalud")
public class PrestadorSaludSoapDto {

    private Long id;
    private String nombre;
    private String direccion;
    private TipoPrestador tipo;
    private String fechaRegistro;

    public PrestadorSaludSoapDto() {
    }

    private PrestadorSaludSoapDto(Long id, String nombre, String direccion, TipoPrestador tipo, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipo = tipo;
        this.fechaRegistro = fechaRegistro;
    }

    public static PrestadorSaludSoapDto fromEntity(PrestadorSalud prestador) {
        String fecha = prestador.getRegDate() != null
                ? prestador.getRegDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;
        return new PrestadorSaludSoapDto(
                prestador.getId(),
                prestador.getNombre(),
                prestador.getDireccion(),
                prestador.getTipo(),
                fecha
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

    public String getFechaRegistro() {
        return fechaRegistro;
    }
}
