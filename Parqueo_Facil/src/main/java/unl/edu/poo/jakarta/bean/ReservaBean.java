package unl.edu.poo.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Reserva;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;
import java.util.*;

@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reserva reserva;
    private Usuario usuarioLogueado;
    private Long idEspacio;
    private Long idUsuario;


    private List<Espacio> espacios;
    private Map<String, List<Espacio>> espaciosPorUbicacion;

    private EntityManagerFactory emf;

    public ReservaBean() {
        emf = Persistence.createEntityManagerFactory("reservaPU");
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        EntityManager em = emf.createEntityManager();

        try {
            espacios = em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();

            espaciosPorUbicacion = new HashMap<>();
            for (Espacio e : espacios) {
                espaciosPorUbicacion
                        .computeIfAbsent(e.getUbicacion(), k -> new ArrayList<>())
                        .add(e);
            }

            reserva = new Reserva();

            usuarioLogueado = (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .get("usuario");
            if (usuarioLogueado == null) {
                System.out.println("Usuario logueado es null");
            } else if (usuarioLogueado.getId() == null) {
                System.out.println("Usuario logueado ID es null");
            } else {
                System.out.println("Usuario logueado ID: " + usuarioLogueado.getId());
            }

            // idUsuario no es necesario, usamos usuarioLogueado directamente
        } finally {
            em.close();
        }
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Long getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(Long idEspacio) {
        this.idEspacio = idEspacio;
    }

    public List<Espacio> getEspacios() {
        return espacios;
    }

    public Map<String, List<Espacio>> getEspaciosPorUbicacion() {
        return espaciosPorUbicacion;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void guardar() {
        if (usuarioLogueado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no identificado. Por favor inicie sesión."));
            return;
        }

        EntityManager em = emf.createEntityManager();

        try {
            if (reserva.getFecha() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(reserva.getFecha());
                cal.add(Calendar.DATE, 1);
                reserva.setFecha(cal.getTime());
            }

            em.getTransaction().begin();

            if (usuarioLogueado == null || usuarioLogueado.getId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no identificado o inválido"));
                return;
            }

            if (idEspacio == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un espacio"));
                return;
            }


            Usuario u = em.find(Usuario.class, usuarioLogueado.getId());
            Espacio e = em.find(Espacio.class, idEspacio);

            if (e == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un espacio válido."));
                em.getTransaction().rollback();
                return;
            }

            reserva.setUsuario(u);
            reserva.setEspacio(e);

            em.persist(reserva);
            em.getTransaction().commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva guardada exitosamente", null));

            reserva = new Reserva();
            idEspacio = null;

        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar la reserva", null));
        } finally {
            em.close();
        }
    }
    public void verificarSesion() {
        Object user = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get("usuario");
        System.out.println("Usuario en sesión: " + user);
    }

}
