package unl.edu.poo.jakarta.bean;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Usuario;
import java.io.Serializable;

@Named
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    // Atributos
    private String usuario;
    private String contrasenia;
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");

    // Método de autenticación
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
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put("usuario", resultado);

                return "/vistas/reserva.xhtml?faces-redirect=true";
            }
        } catch (NoResultException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        FacesContext.getCurrentInstance()
                .addMessage(null,
                        new FacesMessage("Usuario o contraseña incorrectos"));

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