package PrestadorSalud.service;

import jakarta.ejb.Remote;
import PrestadorSalud.model.PrestadorSalud;

import java.util.List;

@Remote
public interface PrestadorSaludServiceRemote {
    void addPrestador(PrestadorSalud prestador);
    List<PrestadorSalud> obtenerPrestadores();
    List<PrestadorSalud> buscarPorNombre(String nombre);
}
