package unl.edu.poo.jakarta.bean;

import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginAdminBean implements Serializable {

    private String usuario;
    private String contrasena;
    private boolean autenticado = false;

    public String login() {
        if ("admin".equals(usuario) && "admin123".equals(contrasena)) {
            autenticado = true;
            return "/vistas/seccionAdmin.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().put("mensaje", "Credenciales incorrectas");
            return "/vistas/login.xhtml?faces-redirect=true";
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/vistas/login.xhtml?faces-redirect=true";
    }

    // Getters y setters
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

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}
