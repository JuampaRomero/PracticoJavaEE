package PrestadorSalud.repository;

import jakarta.ejb.Local;
import PrestadorSalud.model.PrestadorSalud;

import java.util.List;

@Local
public interface PrestadorSaludRepositoryLocal {
    void add(PrestadorSalud prestador);
    List<PrestadorSalud> getAll();
    List<PrestadorSalud> findByNombre(String nombre);
}
