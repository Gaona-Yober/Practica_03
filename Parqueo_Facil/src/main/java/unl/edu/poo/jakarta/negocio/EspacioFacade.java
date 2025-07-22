package unl.edu.poo.jakarta.negocio;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.poo.jakarta.modelo.Espacio;

import java.util.List;

@Stateless
public class EspacioFacade {

    @PersistenceContext(unitName = "reservaPU")
    private EntityManager em;

    public void guardar(Espacio espacio) {
        em.persist(espacio);
    }

    public void actualizar(Espacio espacio) {
        em.merge(espacio);
    }

    public void eliminar(Long id) {
        Espacio e = em.find(Espacio.class, id);
        if (e != null) {
            em.remove(e);
        }
    }

    public List<Espacio> listar() {
        return em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();
    }
}
