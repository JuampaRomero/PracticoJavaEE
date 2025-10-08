package PrestadorSalud.service;

import jakarta.ejb.Local;
import PrestadorSalud.model.PrestadorSalud;

import java.util.List;

@Local
public interface PrestadorSaludServiceLocal {
    void addPrestador(PrestadorSalud prestador);
    List<PrestadorSalud> obtenerPrestadores();
    List<PrestadorSalud> buscarPorNombre(String nombre);
}
