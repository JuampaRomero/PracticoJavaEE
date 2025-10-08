package PrestadorSalud.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestadores_salud")
public class PrestadorSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 180)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoPrestador tipo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime regDate;

    public PrestadorSalud() {
    }

    public PrestadorSalud(String nombre, String direccion, TipoPrestador tipo) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipo = tipo;
        this.regDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoPrestador getTipo() {
        return tipo;
    }

    public void setTipo(TipoPrestador tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    @PrePersist
    public void prePersist() {
        if (regDate == null) {
            regDate = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "PrestadorSalud{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tipo=" + tipo +
                ", regDate=" + regDate +
                '}';
    }
}
