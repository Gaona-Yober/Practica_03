package unl.edu.poo.jakarta.negocio;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import unl.edu.poo.jakarta.modelo.Usuario;

@Stateless
public class SecurityFacade {

    @PersistenceContext(unitName = "reservaPU")
    private EntityManager em;

    public Usuario autenticar(String nombre, String contrasenia) throws Exception {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombre = :nombre AND u.contrasenia = :contrasenia",
                    Usuario.class
            );
            query.setParameter("nombre", nombre);
            query.setParameter("contrasenia", contrasenia);

            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new Exception("Usuario o contraseña incorrectos");
        }
    }
}
