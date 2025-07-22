package unl.edu.poo.jakarta.dao;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.poo.jakarta.modelo.Espacio;

import java.util.List;

@RequestScoped
public class EspacioDAO {

    @PersistenceContext(unitName = "reservaPU")
    private EntityManager em;

    public List<Espacio> obtenerTodos() {
        return em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();
    }

    public Espacio obtenerPorId(Long id) {
        return em.find(Espacio.class, id);
    }
}
