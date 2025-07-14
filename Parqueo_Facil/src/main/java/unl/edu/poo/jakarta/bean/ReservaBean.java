package unl.edu.poo.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Reserva;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("reservaPU");

    // Atrributos
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

    public ReservaBean() {}

    @PostConstruct
    public void init() {
        EntityManager em = emf.createEntityManager();
        try {
            // Cargar espacios
            espacios = em.createQuery("SELECT e FROM Espacio e", Espacio.class).getResultList();

            // Organizar espacios por ubicación
            espaciosPorUbicacion = new HashMap<>();
            for (Espacio e : espacios) {
                espaciosPorUbicacion
                        .computeIfAbsent(e.getUbicacion(), k -> new ArrayList<>())
                        .add(e);
            }

            // Inicializar reserva
            reserva = new Reserva();

            // Obtener usuario logueado
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
        } finally {
            em.close();
        }
    }

    // Métodos
    public void guardar() {
        // Validaciones
        if (horarioSeleccionado == null || horarioSeleccionado.isEmpty()) {
            showError("Error", "Debe seleccionar un horario");
            return;
        }

        if (usuarioLogueado == null) {
            showError("Error", "Usuario no identificado. Por favor inicie sesión.");
            return;
        }

        if (horarioSeleccionado == null || diaSeleccionado == null) {
            showError("Error", "Seleccione día y horario");
            return;
        }

        // Configurar fecha de la reserva
        LocalDate localDate = LocalDate.parse(diaSeleccionado);
        LocalTime horaInicio = getHoraInicioByHorario(horarioSeleccionado);

        if (horaInicio == null) {
            showError("Error", "Horario inválido");
            return;
        }

        LocalDateTime ldt = LocalDateTime.of(localDate, horaInicio);
        reserva.setFecha(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Validar usuario
            if (usuarioLogueado.getId() == null) {
                showError("Error", "Usuario no identificado o inválido");
                return;
            }

            // Validar espacio
            if (idEspacio == null) {
                showError("Error", "Debe seleccionar un espacio");
                return;
            }

            // Obtener entidades relacionadas
            Usuario u = em.find(Usuario.class, usuarioLogueado.getId());
            Espacio e = em.find(Espacio.class, idEspacio);

            if (e == null) {
                showError("Error", "Seleccione un espacio válido.");
                em.getTransaction().rollback();
                return;
            }

            // Configurar reserva
            reserva.setUsuario(u);
            reserva.setEspacio(e);

            // Persistir
            em.persist(reserva);
            em.getTransaction().commit();

            // Mensaje de éxito y limpieza
            showInfo("Reserva guardada exitosamente");
            resetForm();

        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
            showError("Error", "Error al guardar la reserva");
        } finally {
            em.close();
        }
    }

    // Métodos de actualización AJAX
    public void actualizarDiasDisponibles(AjaxBehaviorEvent event) {
        if (idEspacio != null) {
            // Simulación de días disponibles
            diasDisponibles = new ArrayList<>(Arrays.asList(
                    "2025-07-11", "2025-07-12", "2025-07-13"
            ));
            horariosDisponibles.clear();
        }
        diasDisponibles.clear();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            diasDisponibles.add(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        }
        horariosDisponibles.clear();
    }

    public void actualizarHorariosDisponibles(AjaxBehaviorEvent event) {
        this.horariosDisponibles = obtenerHorariosDisponibles(idEspacio, diaSeleccionado);
    }

    // Métodos auxiliares
    private LocalTime getHoraInicioByHorario(String horario) {
        switch (horario) {
            case "00:00 - 06:00": return LocalTime.of(0, 0);
            case "06:00 - 12:00": return LocalTime.of(6, 0);
            case "12:00 - 18:00": return LocalTime.of(12, 0);
            case "18:00 - 00:00": return LocalTime.of(18, 0);
            default: return null;
        }
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

            // Obtener reservas existentes
            List<Reserva> reservas = em.createQuery(
                            "SELECT r FROM Reserva r WHERE r.espacio.id = :idEspacio AND r.fecha >= :inicio AND r.fecha < :fin",
                            Reserva.class)
                    .setParameter("idEspacio", idEspacio)
                    .setParameter("inicio", fechaInicio)
                    .setParameter("fin", fechaFin)
                    .getResultList();

            // Todos los bloques posibles
            List<String> bloques = new ArrayList<>(Arrays.asList(
                    "00:00 - 06:00", "06:00 - 12:00",
                    "12:00 - 18:00", "18:00 - 00:00"
            ));

            // Eliminar bloques ocupados
            for (Reserva r : reservas) {
                bloques.remove(obtenerBloquePorHora(r.getFecha()));
            }

            return bloques;
        } finally {
            em.close();
        }
    }

    private void resetForm() {
        reserva = new Reserva();
        idEspacio = null;
        diaSeleccionado = null;
        horarioSeleccionado = null;
        diasDisponibles = new ArrayList<>();
        horariosDisponibles = new ArrayList<>();
    }

    private void showError(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    private void showInfo(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    // Getters y Setters
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public Long getIdEspacio() { return idEspacio; }
    public void setIdEspacio(Long idEspacio) { this.idEspacio = idEspacio; }
    public List<Espacio> getEspacios() { return espacios; }
    public Map<String, List<Espacio>> getEspaciosPorUbicacion() { return espaciosPorUbicacion; }
    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public List<String> getDiasDisponibles() { return diasDisponibles; }
    public String getDiaSeleccionado() { return diaSeleccionado; }
    public void setDiaSeleccionado(String diaSeleccionado) { this.diaSeleccionado = diaSeleccionado; }
    public List<String> getHorariosDisponibles() { return horariosDisponibles; }
    public String getHorarioSeleccionado() { return horarioSeleccionado; }
    public void setHorarioSeleccionado(String horarioSeleccionado) { this.horarioSeleccionado = horarioSeleccionado; }
}