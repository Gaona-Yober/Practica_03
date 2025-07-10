package unl.edu.poo.jakarta.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Espacio;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class EspacioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Espacio espacio = new Espacio();
    private Espacio espacioSeleccionado = new Espacio();

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");

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

    // Crear nuevo espacio
    public void guardar() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(espacio);
            em.getTransaction().commit();
            espacio = new Espacio(); // limpiar formulario
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Llenar para editar
    public void editar(Espacio espacio) {
        this.espacioSeleccionado = espacio;
    }

    // Actualizar espacio
    public void actualizar() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(espacioSeleccionado);
            em.getTransaction().commit();
            espacioSeleccionado = new Espacio(); // limpiar formulario
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Listar todos los espacios
    public List<Espacio> getEspacios() {
        EntityManager em = emf.createEntityManager();
        List<Espacio> lista = em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();
        em.close();
        return lista;
    }

    // (Opcional) Eliminar espacio
    public void eliminar(Espacio espacio) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Espacio e = em.find(Espacio.class, espacio.getId());
            if (e != null) {
                em.remove(e);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

}
