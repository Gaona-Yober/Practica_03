package unl.edu.poo.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.poo.jakarta.modelo.Usuario;
import unl.edu.poo.jakarta.service.UsuarioService;

import java.io.Serializable;

@Named("usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario usuario = new Usuario();

    @Inject
    private UsuarioService usuarioService;

    public void guardar() {
        try {
            if (usuarioService.existeNombreUsuario(usuario.getNombre())) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "El nombre de usuario ya está en uso");
                return;
            }

            usuarioService.guardar(usuario);

            mostrarMensaje(FacesMessage.SEVERITY_INFO,
                    "Usuario registrado correctamente");

            usuario = new Usuario(); // Limpiar formulario

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error al registrar el usuario");
        }
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
