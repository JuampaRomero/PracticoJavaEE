package com.ejercicio1.ws.rest.dto;

import java.time.LocalDate;

/**
 * DTO para transferencia de datos de Trabajador
 * Se usa tanto para request como response
 */
public class TrabajadorDTO {
    
    private String cedula;
    private String nombre;
    private String apellido;
    private String especialidad;
    private Integer matriculaProfesional;
    private LocalDate fechaIngreso;
    private boolean activo;
    
    // Constructor vacío
    public TrabajadorDTO() {
    }
    
    // Constructor completo
    public TrabajadorDTO(String cedula, String nombre, String apellido, 
                        String especialidad, Integer matriculaProfesional, 
                        LocalDate fechaIngreso, boolean activo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.matriculaProfesional = matriculaProfesional;
        this.fechaIngreso = fechaIngreso;
        this.activo = activo;
    }
    
    // Getters y Setters
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public Integer getMatriculaProfesional() {
        return matriculaProfesional;
    }
    
    public void setMatriculaProfesional(Integer matriculaProfesional) {
        this.matriculaProfesional = matriculaProfesional;
    }
    
    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
    
    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}