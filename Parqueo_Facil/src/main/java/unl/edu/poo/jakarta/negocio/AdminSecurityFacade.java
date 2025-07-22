package unl.edu.poo.jakarta.negocio;

import jakarta.ejb.Stateless;

@Stateless
public class AdminSecurityFacade {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    public boolean autenticar(String usuario, String contrasena) {
        return ADMIN_USER.equals(usuario) && ADMIN_PASS.equals(contrasena);
    }
}
