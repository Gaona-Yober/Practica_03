package unl.edu.poo.jakarta.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Reserva;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import jakarta.faces.event.AjaxBehaviorEvent;

@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reserva reserva;
    private Usuario usuarioLogueado;
    private Long idEspacio;
    private Long idUsuario;
    private String diaSeleccionado;
    private String horarioSeleccionado;


    private List<Espacio> espacios;
    private Map<String, List<Espacio>> espaciosPorUbicacion;
    private List<String> diasDisponibles = new ArrayList<>();
    private List<String> horariosDisponibles = new ArrayList<>();

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

    public void guardar() throws ParseException {

        if (horarioSeleccionado == null || horarioSeleccionado.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un horario"));
            return;
        }

        if (usuarioLogueado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario no identificado. Por favor inicie sesión."));
            return;
        }
        if (horarioSeleccionado == null || diaSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Seleccione día y horario", null));
            return;
        }
        /*espués de guardar exitosamente
        reserva = new Reserva();
        idEspacio = null;
        diaSeleccionado = null;
        horarioSeleccionado = null;
        diasDisponibles = new ArrayList<>();
        horariosDisponibles = new ArrayList<>();*/

        // Convertir día y bloque horario a Date final con hora
        LocalDate localDate = LocalDate.parse(diaSeleccionado);
        LocalTime horaInicio;

        switch (horarioSeleccionado) {
            case "00:00 - 06:00": horaInicio = LocalTime.of(0, 0); break;
            case "06:00 - 12:00": horaInicio = LocalTime.of(6, 0); break;
            case "12:00 - 18:00": horaInicio = LocalTime.of(12, 0); break;
            case "18:00 - 00:00": horaInicio = LocalTime.of(18, 0); break;
            default:
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Horario inválido", null));
                return;
        }

        LocalDateTime ldt = LocalDateTime.of(localDate, horaInicio);
        reserva.setFecha(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));


        EntityManager em = emf.createEntityManager();

        try {
            if (reserva.getFecha() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(reserva.getFecha());
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
        //reserva.setHorarioSeleccionado(horarioSeleccionado);
        //reserva.setFecha(new SimpleDateFormat("yyyy-MM-dd").parse(diaSeleccionado);
    }

    public void actualizarDiasDisponibles(jakarta.faces.event.AjaxBehaviorEvent event) {
        if (idEspacio != null) {
            // Aquí puedes simular la lógica si no tienes el servicio real
            diasDisponibles = new ArrayList<>(Arrays.asList("2025-07-11", "2025-07-12", "2025-07-13"));
            horariosDisponibles.clear();
        }
        // Simulación de lógica: suponer que todos los días siguientes 7 días están disponibles
        diasDisponibles.clear();

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date date = cal.getTime();
            diasDisponibles.add(new java.text.SimpleDateFormat("yyyy-MM-dd").format(date));
        }

        horariosDisponibles.clear(); // Limpiar horarios
    }

    public void actualizarHorariosDisponibles(AjaxBehaviorEvent event) {
        // El valor ya está asignado por JSF en diaSeleccionado
        this.horariosDisponibles = obtenerHorariosDisponibles(idEspacio, diaSeleccionado);
    }


    private String obtenerBloquePorHora(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        int hora = cal.get(Calendar.HOUR_OF_DAY);

        if (hora >= 0 && hora < 6) return "00:00 - 06:00";
        if (hora >= 6 && hora < 12) return "06:00 - 12:00";
        if (hora >= 12 && hora < 18) return "12:00 - 18:00";
        return "18:00 - 00:00";
    }



    private List<String> obtenerHorariosDisponibles(Long idEspacio, String dia) {
        EntityManager em = emf.createEntityManager();
        try {
            Date fechaInicio = java.sql.Date.valueOf(dia);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaInicio);
            cal.add(Calendar.DATE, 1);
            Date fechaFin = cal.getTime();

            List<Reserva> reservas = em.createQuery(
                            "SELECT r FROM Reserva r WHERE r.espacio.id = :idEspacio AND r.fecha >= :inicio AND r.fecha < :fin",
                            Reserva.class
                    )
                    .setParameter("idEspacio", idEspacio)
                    .setParameter("inicio", fechaInicio)
                    .setParameter("fin", fechaFin)
                    .getResultList();

            List<String> bloques = new ArrayList<>(List.of(
                    "00:00 - 06:00", "06:00 - 12:00",
                    "12:00 - 18:00", "18:00 - 00:00"
            ));

            for (Reserva r : reservas) {
                String bloque = obtenerBloquePorHora(r.getFecha());
                bloques.remove(bloque); // elimina el bloque ocupado
            }

            return bloques;

        } finally {
            em.close();
        }
    }



    private List<String> generarHorariosDe6Horas() {
        return List.of("06:00 - 12:00", "12:00 - 18:00", "18:00 - 00:00", "00:00 - 06:00");
    }




    //get y set
    public List<String> getDiasDisponibles() {return diasDisponibles;}

    public String getDiaSeleccionado() {return diaSeleccionado;}

    public void setDiaSeleccionado(String diaSeleccionado) {this.diaSeleccionado = diaSeleccionado;}

    public List<String> getHorariosDisponibles() {return horariosDisponibles;}

    public String getHorarioSeleccionado() {return horarioSeleccionado;}

    public void setHorarioSeleccionado(String horarioSeleccionado) {this.horarioSeleccionado = horarioSeleccionado;}


}
