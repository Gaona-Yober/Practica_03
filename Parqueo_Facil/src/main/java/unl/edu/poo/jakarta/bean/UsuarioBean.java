package unl.edu.poo.jakarta.bean;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@ManagedBean
public class UsuarioBean implements Serializable {
    @PersistenceContext(unitName = "reservaPU")
    private EntityManager entityManager;

    private Usuario usuario = new Usuario();
    private Usuario em;

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");

    public Usuario getUsuario() {return usuario;}

    public void setUsuario(Usuario usuario) {this.usuario = usuario;}

    public void guardar() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");
        EntityManager em = emf.createEntityManager();

        try {
            // Verificar si ya existe un usuario con ese nombre
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombre = :nombre", Usuario.class);
            query.setParameter("nombre", usuario.getNombre());

            if (!query.getResultList().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "El nombre de usuario ya está en uso", null));
                return;
            }

            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Usuario registrado correctamente", null));

            usuario = new Usuario(); // limpiar formulario

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al registrar el usuario", null));
        } finally {
            em.close();
            emf.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        return entityManager.find(Usuario.class, id);
    }



}