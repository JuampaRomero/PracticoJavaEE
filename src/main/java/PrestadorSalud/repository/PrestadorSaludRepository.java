package PrestadorSalud.repository;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import PrestadorSalud.config.LocalDatabaseProvisioner;
import PrestadorSalud.model.PrestadorSalud;

import java.util.List;
import java.util.Objects;

@Stateless
@Local(PrestadorSaludRepositoryLocal.class)
@Remote(PrestadorSaludRepositoryRemote.class)
public class PrestadorSaludRepository implements PrestadorSaludRepositoryLocal, PrestadorSaludRepositoryRemote {

    @PersistenceContext(unitName = "PrestadorPU")
    private EntityManager entityManager;

    private static EntityManagerFactory localEntityManagerFactory;
    private EntityManager fallbackEntityManager;

    // Constructor utilizado por el contenedor
    public PrestadorSaludRepository() {
    }

    // Constructor alternativo para escenarios fuera del contenedor (p. ej. aplicación consola)
    public PrestadorSaludRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.fallbackEntityManager = entityManager;
    }

    @Override
    public void add(PrestadorSalud prestador) {
        EntityManager em = entityManager();

        boolean joinedToTransaction;
        try {
            joinedToTransaction = em.isJoinedToTransaction();
        } catch (IllegalStateException ex) {
            // EntityManager administrado por contenedor sin acceso al estado de la transacción
            joinedToTransaction = true;
        }

        boolean localTransaction = false;
        EntityTransaction transaction = null;
        try {
            if (!joinedToTransaction) {
                transaction = em.getTransaction();
                transaction.begin();
                localTransaction = true;
            }
            em.persist(prestador);
            if (localTransaction) {
                transaction.commit();
            }
        } catch (RuntimeException ex) {
            if (localTransaction && transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    @Override
    public List<PrestadorSalud> getAll() {
        EntityManager em = entityManager();
        return em.createQuery(
                "SELECT p FROM PrestadorSalud p ORDER BY p.regDate DESC",
                PrestadorSalud.class
        ).getResultList();
    }

    @Override
    public List<PrestadorSalud> findByNombre(String nombre) {
        EntityManager em = entityManager();
        String valor = Objects.requireNonNullElse(nombre, "").trim();
        if (valor.isEmpty()) {
            return getAll();
        }
        return em.createQuery(
                "SELECT p FROM PrestadorSalud p WHERE LOWER(p.nombre) = :nombre",
                PrestadorSalud.class
        ).setParameter("nombre", valor.toLowerCase())
                .getResultList();
    }

    private EntityManager entityManager() {
        if (entityManager != null) {
            return entityManager;
        }
        if (fallbackEntityManager == null) {
            synchronized (PrestadorSaludRepository.class) {
                if (fallbackEntityManager == null) {
                    LocalDatabaseProvisioner.ensureDatabaseExists();
                    if (localEntityManagerFactory == null) {
                        localEntityManagerFactory = Persistence.createEntityManagerFactory(
                                "PrestadorLocalPU",
                                LocalDatabaseProvisioner.persistenceOverrides()
                        );
                    }
                    fallbackEntityManager = localEntityManagerFactory.createEntityManager();
                }
            }
        }
        return fallbackEntityManager;
    }
}
