package unl.edu.poo.jakarta.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;
import unl.edu.poo.jakarta.modelo.Usuario;
import unl.edu.poo.jakarta.negocio.SecurityFacade;
import unl.edu.poo.jakarta.sesion.UserSession;
import unl.edu.poo.jakarta.util.FacesUtil;
import unl.edu.poo.jakarta.security.UserPrincipal;

import java.io.Serializable;

@Named
@ViewScoped
public class LoginBean implements Serializable {

    private String usuario;
    private String contrasenia;

    @Inject
    private SecurityFacade securityFacade;

    @Inject
    private UserSession userSession;

    public String login() {
        try {
            Usuario u = securityFacade.autenticar(usuario, contrasenia);
            userSession.login(u);

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .put("usuario", u);

            FacesUtil.mensajeInfo("Bienvenido " + u.getNombre());
            return "/vistas/reserva.xhtml?faces-redirect=true";
        } catch (Exception e) {
            FacesUtil.mensajeError(e.getMessage());
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        userSession.logout();
        FacesUtil.mensajeInfo("Sesión cerrada exitosamente");
        return "/login.xhtml?faces-redirect=true";
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
