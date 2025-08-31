package com.ejercicio1.entities;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Entidad que representa un Trabajador de la Salud
 * Atributos de diferentes tipos según el requisito del ejercicio
 */
public class TrabajadorSalud implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Atributo tipo String
    private String cedula;  // Identificador único
    
    // Atributo tipo String
    private String nombre;
    
    // Atributo tipo String
    private String apellido;
    
    // Atributo tipo String
    private String especialidad;
    
    // Atributo tipo numérico (Integer)
    private Integer matriculaProfesional;
    
    // Atributo tipo fecha (LocalDate)
    private LocalDate fechaIngreso;
    
    // Atributo tipo boolean
    private boolean activo;
    
    // Constructor vacío
    public TrabajadorSalud() {
    }
    
    // Constructor con parámetros
    public TrabajadorSalud(String cedula, String nombre, String apellido, 
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
    
    @Override
    public String toString() {
        return "TrabajadorSalud{" +
                "cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", matriculaProfesional=" + matriculaProfesional +
                ", fechaIngreso=" + fechaIngreso +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        TrabajadorSalud that = (TrabajadorSalud) o;
        
        return cedula != null ? cedula.equals(that.cedula) : that.cedula == null;
    }
    
    @Override
    public int hashCode() {
        return cedula != null ? cedula.hashCode() : 0;
    }
}
