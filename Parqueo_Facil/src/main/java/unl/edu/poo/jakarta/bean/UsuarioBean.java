package unl.edu.poo.jakarta.bean;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Usuario;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
@ManagedBean
public class UsuarioBean implements Serializable {
    @PersistenceContext(unitName = "reservaPU")
    private EntityManager entityManager;

    // Fábrica de EntityManager (para operaciones fuera de contenedor)
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");

    private Usuario usuario = new Usuario();
    private Usuario em;

    // MÉTODOS PRINCIPALES
    public void guardar() {
        EntityManager em = emf.createEntityManager();
        try {
            // Validar si el nombre de usuario ya existe
            if (existeNombreUsuario(usuario.getNombre(), em)) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "El nombre de usuario ya está en uso");
                return;
            }

            // Persistir el usuario
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();

            mostrarMensaje(FacesMessage.SEVERITY_INFO,
                    "Usuario registrado correctamente");

            // Limpiar el formulario
            usuario = new Usuario();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error al registrar el usuario");
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(Long id) {
        return entityManager.find(Usuario.class, id);
    }

    // MÉTODOS AUXILIARES
    private boolean existeNombreUsuario(String nombre, EntityManager em) {
        TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nombre = :nombre",
                Usuario.class);
        query.setParameter("nombre", nombre);
        return !query.getResultList().isEmpty();
    }

    private void mostrarMensaje(FacesMessage.Severity severidad, String mensaje) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severidad, mensaje, null));
    }


    // Getters y Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}