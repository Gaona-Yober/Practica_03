package unl.edu.poo.jakarta.security;

import unl.edu.poo.jakarta.modelo.Usuario;

public class UserPrincipal {

    private Usuario usuario;

    public UserPrincipal(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return usuario.getNombre();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
