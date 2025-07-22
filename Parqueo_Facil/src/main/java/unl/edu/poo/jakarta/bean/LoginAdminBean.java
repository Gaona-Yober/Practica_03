package unl.edu.poo.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.poo.jakarta.negocio.AdminSecurityFacade;
import unl.edu.poo.jakarta.sesion.AdminSession;
import unl.edu.poo.jakarta.util.FacesUtil;

import java.io.Serializable;

@Named
@ViewScoped
public class LoginAdminBean implements Serializable {

    private String usuario;
    private String contrasena;

    @Inject
    private AdminSecurityFacade adminSecurity;

    @Inject
    private AdminSession adminSession;

    public String login() {
        if (adminSecurity.autenticar(usuario, contrasena)) {
            adminSession.login();
            FacesUtil.mensaajeInfo("Bienvenido, Administrador");
            return "/vistas/seccionAdmin.xhtml?faces-redirect=true";
        } else {
            FacesUtil.mensaajeError("Credenciales incorrectas");
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        adminSession.logout();
        return "/vistas/login.xhtml?faces-redirect=true";
    }

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
