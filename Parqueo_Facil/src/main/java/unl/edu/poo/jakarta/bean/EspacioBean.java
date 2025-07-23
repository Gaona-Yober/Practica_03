package unl.edu.poo.jakarta.bean;

import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.negocio.EspacioFacade;
import unl.edu.poo.jakarta.util.FacesUtil;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class EspacioBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Espacio espacio = new Espacio();
    private Espacio espacioSeleccionado;

    @EJB
    private EspacioFacade espacioFacade;

    public void guardar() {
        try {
            espacioFacade.guardar(espacio);
            espacio = new Espacio();
            FacesUtil.mensajeInfo("Espacio creado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.mensajeError("Error al crear espacio");
        }
    }

    public void actualizar() {
        try {
            espacioFacade.actualizar(espacioSeleccionado);
            espacioSeleccionado = null;
            FacesUtil.mensajeInfo("Espacio actualizado");
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.mensajeError("Error al actualizar");
        }
    }

    public void eliminar(Espacio espacio) {
        espacioFacade.eliminar(espacio.getId());
    }

    public List<Espacio> getEspacios() {
        return espacioFacade.listar();
    }

    public void editar(Espacio espacio) {
        this.espacioSeleccionado = espacio;
    }

    // Getters y Setters
    public Espacio getEspacio() {
        return espacio;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }

    public Espacio getEspacioSeleccionado() {
        return espacioSeleccionado;
    }

    public void setEspacioSeleccionado(Espacio espacioSeleccionado) {
        this.espacioSeleccionado = espacioSeleccionado;
    }
}
