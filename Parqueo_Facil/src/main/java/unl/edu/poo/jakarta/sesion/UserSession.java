package unl.edu.poo.jakarta.sesion;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private Usuario usuario;

    public void login(Usuario usuario) {
        this.usuario = usuario;
    }

    public void logout() {
        this.usuario = null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public boolean estaAutenticado() {
        return usuario != null;
    }
}
