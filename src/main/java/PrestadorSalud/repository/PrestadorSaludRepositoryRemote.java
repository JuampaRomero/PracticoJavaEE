package PrestadorSalud.repository;

import jakarta.ejb.Remote;
import PrestadorSalud.model.PrestadorSalud;

import java.util.List;

@Remote
public interface PrestadorSaludRepositoryRemote {
    void add(PrestadorSalud prestador);
    List<PrestadorSalud> getAll();
    List<PrestadorSalud> findByNombre(String nombre);
}
