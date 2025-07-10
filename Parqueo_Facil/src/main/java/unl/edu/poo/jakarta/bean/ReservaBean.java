package unl.edu.poo.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Reserva;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reserva reserva;

    private Long idUsuario;
    private Long idEspacio;

    private EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        reserva = new Reserva();
        emf = Persistence.createEntityManagerFactory("reservaPU");
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(Long idEspacio) {
        this.idEspacio = idEspacio;
    }

    public void guardar() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");
        EntityManager em = emf.createEntityManager();

        try {
            if (reserva.getFecha() != null) {
                // Sumar 1 día a la fecha
                Calendar cal = Calendar.getInstance();
                cal.setTime(reserva.getFecha());
                cal.add(Calendar.DATE, 1); // aquí sumas 1 día
                reserva.setFecha(cal.getTime());
            }
            em.getTransaction().begin();

            // Buscar usuario y espacio antes de persistir
            Usuario u = em.find(Usuario.class, idUsuario);
            Espacio e = em.find(Espacio.class, idEspacio);
            reserva.setUsuario(u);
            reserva.setEspacio(e);

            em.persist(reserva);
            em.getTransaction().commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva guardada exitosamente", null));

            // Limpia el formulario
            reserva = new Reserva();
            idUsuario = null;
            idEspacio = null;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar la reserva", null));
        } finally {
            em.close();
            emf.close();
        }
    }

    public List<Usuario> getUsuarios() {
        EntityManager em = emf.createEntityManager();
        List<Usuario> lista = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        em.close();
        return lista;
    }

    public List<Espacio> getEspacios() {
        EntityManager em = emf.createEntityManager();
        List<Espacio> lista = em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();
        em.close();
        return lista;
    }
}
