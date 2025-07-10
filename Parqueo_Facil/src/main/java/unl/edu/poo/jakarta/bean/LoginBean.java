package unl.edu.poo.jakarta.bean;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;

@Named
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private String usuario;
    private String contrasenia;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");

    public String verificarLogin() {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombre = :usuario AND u.contrasenia = :contrasenia",
                    Usuario.class);
            query.setParameter("usuario", usuario);
            query.setParameter("contrasenia", contrasenia);

            Usuario resultado = query.getSingleResult();

            if (resultado != null) {
                // guardar el usuario en la sesión con la clave "usuario"
                jakarta.faces.context.FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put("usuario", resultado);

                return "/vistas/reserva.xhtml?faces-redirect=true";
            }
        } catch (NoResultException e) {
            // Usuario no encontrado
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        jakarta.faces.context.FacesContext.getCurrentInstance()
                .addMessage(null, new jakarta.faces.application.FacesMessage("Usuario o contraseña incorrectos"));

        return null;
    }

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

}
