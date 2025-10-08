package PrestadorSalud.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import PrestadorSalud.model.PrestadorSalud;
import PrestadorSalud.repository.PrestadorSaludRepositoryLocal;

import java.util.List;

@Stateless
@Local(PrestadorSaludServiceLocal.class)
@Remote(PrestadorSaludServiceRemote.class)
public class PrestadorSaludService implements PrestadorSaludServiceLocal, PrestadorSaludServiceRemote {

    @EJB
    private PrestadorSaludRepositoryLocal repository;

    // Constructor for manual injection (e.g., console application)
    public PrestadorSaludService() {}

    public PrestadorSaludService(PrestadorSaludRepositoryLocal repository) {
        this.repository = repository;
    }

    @Override
    public void addPrestador(PrestadorSalud prestador) {
        if (prestador.getNombre() == null || prestador.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        repository.add(prestador);
    }

    @Override
    public List<PrestadorSalud> obtenerPrestadores() {
        return repository.getAll();
    }

    @Override
    public List<PrestadorSalud> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre);
    }
}
