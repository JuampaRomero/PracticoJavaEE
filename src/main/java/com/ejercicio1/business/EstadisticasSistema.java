package com.ejercicio1.business;

import java.io.Serializable;

/**
 * Clase que representa las estadísticas del sistema.
 */
public class EstadisticasSistema implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int totalTrabajadores;
    private int trabajadoresActivos;
    private int trabajadoresInactivos;
    private String especialidadMasComun;
    private double porcentajeActivos;
    
    // Constructor
    public EstadisticasSistema() {
    }
    
    // Getters y Setters
    public int getTotalTrabajadores() {
        return totalTrabajadores;
    }
    
    public void setTotalTrabajadores(int totalTrabajadores) {
        this.totalTrabajadores = totalTrabajadores;
    }
    
    public int getTrabajadoresActivos() {
        return trabajadoresActivos;
    }
    
    public void setTrabajadoresActivos(int trabajadoresActivos) {
        this.trabajadoresActivos = trabajadoresActivos;
    }
    
    public int getTrabajadoresInactivos() {
        return trabajadoresInactivos;
    }
    
    public void setTrabajadoresInactivos(int trabajadoresInactivos) {
        this.trabajadoresInactivos = trabajadoresInactivos;
    }
    
    public String getEspecialidadMasComun() {
        return especialidadMasComun;
    }
    
    public void setEspecialidadMasComun(String especialidadMasComun) {
        this.especialidadMasComun = especialidadMasComun;
    }
    
    public double getPorcentajeActivos() {
        return porcentajeActivos;
    }
    
    public void setPorcentajeActivos(double porcentajeActivos) {
        this.porcentajeActivos = porcentajeActivos;
    }
}
