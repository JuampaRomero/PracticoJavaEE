package PrestadorSalud.web.soap.dto;

import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrestadorSaludAlta")
public class PrestadorSaludSoapRequest {

    private String nombre;
    private String direccion;
    private String tipo;

    public PrestadorSaludSoapRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public PrestadorSalud toEntity() {
        PrestadorSalud prestador = new PrestadorSalud();
        prestador.setNombre(nombre);
        prestador.setDireccion(direccion);
        prestador.setTipo(parseTipo(tipo));
        return prestador;
    }

    private TipoPrestador parseTipo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        try {
            return TipoPrestador.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de prestador desconocido: " + value);
        }
    }
}
