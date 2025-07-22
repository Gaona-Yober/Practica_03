package unl.edu.poo.jakarta.sesion;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class AdminSession implements Serializable {

    private boolean autenticado = false;

    public void login() {
        autenticado = true;
    }

    public void logout() {
        autenticado = false;
    }

    public boolean isAutenticado() {
        return autenticado;
    }
}
