package com.ejercicio1.web.beans;

import com.ejercicio1.business.BusinessException;
import com.ejercicio1.business.TrabajadorSaludServiceLocal;
import com.ejercicio1.entities.TrabajadorSalud;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Managed Bean para gestionar las operaciones de trabajadores en la interfaz web
 */
@Named("trabajadorBean")
@SessionScoped
public class TrabajadorBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @EJB
    private TrabajadorSaludServiceLocal trabajadorService;
    
    private List<TrabajadorSalud> trabajadores;
    private List<TrabajadorSalud> trabajadoresFiltrados;
    private TrabajadorSalud trabajadorSeleccionado;
    private TrabajadorSalud nuevoTrabajador;
    private String busquedaCedula;
    private String busquedaEspecialidad;
    
    @PostConstruct
    public void init() {
        cargarTrabajadores();
        nuevoTrabajador = new TrabajadorSalud();
        nuevoTrabajador.setFechaIngreso(LocalDate.now());
        nuevoTrabajador.setActivo(true);
    }
    
    /**
     * Carga todos los trabajadores del sistema
     */
    public void cargarTrabajadores() {
        try {
            trabajadores = trabajadorService.obtenerTodos();
        } catch (Exception e) {
            trabajadores = new ArrayList<>();
            addErrorMessage("Error al cargar trabajadores", e.getMessage());
        }
    }
    
    /**
     * Guarda un nuevo trabajador
     */
    public void guardarTrabajador() {
        try {
            trabajadorService.agregarTrabajador(nuevoTrabajador);
            cargarTrabajadores();
            
            addSuccessMessage("Trabajador registrado", 
                "El trabajador " + nuevoTrabajador.getNombre() + " " + 
                nuevoTrabajador.getApellido() + " ha sido registrado exitosamente.");
            
            // Resetear el formulario
            nuevoTrabajador = new TrabajadorSalud();
            nuevoTrabajador.setFechaIngreso(LocalDate.now());
            nuevoTrabajador.setActivo(true);
            
            // Cerrar el diálogo si se está usando uno
            PrimeFaces.current().executeScript("PF('dlgNuevoTrabajador').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:dt-trabajadores");
            
        } catch (BusinessException e) {
            addErrorMessage("Error de validación", e.getMessage());
        } catch (Exception e) {
            addErrorMessage("Error al guardar", "Ocurrió un error al guardar el trabajador: " + e.getMessage());
        }
    }
    
    /**
     * Busca un trabajador por cédula
     */
    public void buscarPorCedula() {
        if (busquedaCedula == null || busquedaCedula.trim().isEmpty()) {
            addWarnMessage("Búsqueda vacía", "Por favor ingrese una cédula para buscar");
            return;
        }
        
        try {
            TrabajadorSalud encontrado = trabajadorService.buscarPorCedula(busquedaCedula);
            if (encontrado != null) {
                trabajadores = new ArrayList<>();
                trabajadores.add(encontrado);
                addInfoMessage("Trabajador encontrado", 
                    "Se encontró al trabajador: " + encontrado.getNombre() + " " + encontrado.getApellido());
            } else {
                trabajadores = new ArrayList<>();
                addWarnMessage("No encontrado", "No se encontró ningún trabajador con la cédula: " + busquedaCedula);
            }
        } catch (Exception e) {
            addErrorMessage("Error en búsqueda", e.getMessage());
        }
    }
    
    /**
     * Busca trabajadores por especialidad
     */
    public void buscarPorEspecialidad() {
        if (busquedaEspecialidad == null || busquedaEspecialidad.trim().isEmpty()) {
            addWarnMessage("Búsqueda vacía", "Por favor seleccione una especialidad para buscar");
            return;
        }
        
        try {
            trabajadores = trabajadorService.buscarPorEspecialidad(busquedaEspecialidad);
            if (trabajadores.isEmpty()) {
                addWarnMessage("Sin resultados", 
                    "No se encontraron trabajadores con la especialidad: " + busquedaEspecialidad);
            } else {
                addInfoMessage("Búsqueda exitosa", 
                    "Se encontraron " + trabajadores.size() + " trabajadores con esa especialidad");
            }
        } catch (Exception e) {
            addErrorMessage("Error en búsqueda", e.getMessage());
        }
    }
    
    /**
     * Limpia los filtros y recarga todos los trabajadores
     */
    public void limpiarFiltros() {
        busquedaCedula = null;
        busquedaEspecialidad = null;
        trabajadoresFiltrados = null;
        cargarTrabajadores();
        addInfoMessage("Filtros limpiados", "Se han limpiado todos los filtros");
    }
    
    /**
     * Prepara el formulario para un nuevo trabajador
     */
    public void prepararNuevoTrabajador() {
        nuevoTrabajador = new TrabajadorSalud();
        nuevoTrabajador.setFechaIngreso(LocalDate.now());
        nuevoTrabajador.setActivo(true);
    }
    
    /**
     * Verifica si hay trabajadores cargados
     */
    public boolean hayTrabajadores() {
        return trabajadores != null && !trabajadores.isEmpty();
    }
    
    /**
     * Obtiene las especialidades únicas para el filtro
     */
    public List<String> getEspecialidades() {
        List<String> especialidades = new ArrayList<>();
        especialidades.add("Medicina General");
        especialidades.add("Pediatría");
        especialidades.add("Cardiología");
        especialidades.add("Neurología");
        especialidades.add("Traumatología");
        especialidades.add("Ginecología");
        especialidades.add("Oftalmología");
        especialidades.add("Dermatología");
        especialidades.add("Psiquiatría");
        especialidades.add("Enfermería");
        return especialidades;
    }
    
    // Métodos de utilidad para mensajes
    private void addSuccessMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }
    
    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }
    
    private void addWarnMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }
    
    private void addInfoMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }
    
    // Getters y Setters
    public List<TrabajadorSalud> getTrabajadores() {
        return trabajadores;
    }
    
    public void setTrabajadores(List<TrabajadorSalud> trabajadores) {
        this.trabajadores = trabajadores;
    }
    
    public List<TrabajadorSalud> getTrabajadoresFiltrados() {
        return trabajadoresFiltrados;
    }
    
    public void setTrabajadoresFiltrados(List<TrabajadorSalud> trabajadoresFiltrados) {
        this.trabajadoresFiltrados = trabajadoresFiltrados;
    }
    
    public TrabajadorSalud getTrabajadorSeleccionado() {
        return trabajadorSeleccionado;
    }
    
    public void setTrabajadorSeleccionado(TrabajadorSalud trabajadorSeleccionado) {
        this.trabajadorSeleccionado = trabajadorSeleccionado;
    }
    
    public TrabajadorSalud getNuevoTrabajador() {
        return nuevoTrabajador;
    }
    
    public void setNuevoTrabajador(TrabajadorSalud nuevoTrabajador) {
        this.nuevoTrabajador = nuevoTrabajador;
    }
    
    public String getBusquedaCedula() {
        return busquedaCedula;
    }
    
    public void setBusquedaCedula(String busquedaCedula) {
        this.busquedaCedula = busquedaCedula;
    }
    
    public String getBusquedaEspecialidad() {
        return busquedaEspecialidad;
    }
    
    public void setBusquedaEspecialidad(String busquedaEspecialidad) {
        this.busquedaEspecialidad = busquedaEspecialidad;
    }
}
