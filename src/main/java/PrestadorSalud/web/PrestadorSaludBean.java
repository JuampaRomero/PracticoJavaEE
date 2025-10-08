package PrestadorSalud.web;

import PrestadorSalud.messaging.PrestadorAltaProducer;
import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.model.TipoPrestador;
import PrestadorSalud.service.PrestadorSaludServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("prestadorSaludBean")
@ViewScoped
public class PrestadorSaludBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @EJB
    private PrestadorSaludServiceLocal service;

    @EJB
    private PrestadorAltaProducer altaProducer;

    private PrestadorSalud nuevoPrestador;
    private String filtroNombre;
    private List<PrestadorSalud> prestadores;

    @PostConstruct
    public void init() {
        nuevoPrestador = new PrestadorSalud();
        prestadores = new ArrayList<>();
        cargarPrestadores();
    }

    public void agregar() {
        try {
            altaProducer.enviarAlta(nuevoPrestador);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Alta en proceso",
                            String.format("%s se está registrando, puede demorar unos segundos", nuevoPrestador.getNombre().trim())));
            nuevoPrestador = new PrestadorSalud();
            filtroNombre = null;
            cargarPrestadores();
        } catch (IllegalArgumentException | IllegalStateException e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.validationFailed();
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo registrar", e.getMessage()));
        }
    }

    public void buscar() {
        cargarPrestadores();
    }

    public void limpiarBusqueda() {
        filtroNombre = null;
        cargarPrestadores();
    }

    private void cargarPrestadores() {
        if (filtroNombre != null && !filtroNombre.isBlank()) {
            prestadores = service.buscarPorNombre(filtroNombre.trim());
        } else {
            prestadores = service.obtenerPrestadores();
        }
    }

    public String formatearRegistro(LocalDateTime fecha) {
        return fecha != null ? fecha.format(DATE_FORMATTER) : "";
    }

    public PrestadorSalud getNuevoPrestador() {
        return nuevoPrestador;
    }

    public void setNuevoPrestador(PrestadorSalud nuevoPrestador) {
        this.nuevoPrestador = nuevoPrestador;
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public List<PrestadorSalud> getPrestadores() {
        return prestadores;
    }

    public TipoPrestador[] getTiposPrestador() {
        return TipoPrestador.values();
    }
}
