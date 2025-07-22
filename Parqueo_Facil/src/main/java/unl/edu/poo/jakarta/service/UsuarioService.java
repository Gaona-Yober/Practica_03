package unl.edu.poo.jakarta.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import unl.edu.poo.jakarta.modelo.Usuario;

@Stateless
public class UsuarioService {

    @PersistenceContext(unitName = "reservaPU")
    private EntityManager em;

    public boolean existeNombreUsuario(String nombre) {
        TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nombre = :nombre", Usuario.class);
        query.setParameter("nombre", nombre);
        return !query.getResultList().isEmpty();
    }

    public void guardar(Usuario usuario) {
        em.persist(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return em.find(Usuario.class, id);
    }
}
