package unl.edu.poo.jakarta.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Reserva;
import unl.edu.poo.jakarta.modelo.Usuario;
import unl.edu.poo.jakarta.service.EspacioService;
import unl.edu.poo.jakarta.service.ReservaService;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Reserva reserva;
    private Long idEspacio;
    private String diaSeleccionado;
    private String horarioSeleccionado;

    private List<Espacio> espacios;
    private Map<String, List<Espacio>> espaciosPorUbicacion;
    private List<String> diasDisponibles = new ArrayList<>();
    private List<String> horariosDisponibles = new ArrayList<>();

    private Usuario usuarioLogueado;

    // Inyecciones
    @Inject
    private ReservaService reservaService;

    @Inject
    private EspacioService espacioService;

    @PostConstruct
    public void init() {
        try {
            usuarioLogueado = (Usuario) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("usuario");

            if (usuarioLogueado == null || usuarioLogueado.getId() == null) {
                System.out.println("Usuario no logueado");
            } else {
                System.out.println("ID usuario logueado: " + usuarioLogueado.getId());
            }

            espacios = espacioService.obtenerTodos();

            espaciosPorUbicacion = new HashMap<>();
            for (Espacio e : espacios) {
                espaciosPorUbicacion.computeIfAbsent(e.getUbicacion(), k -> new ArrayList<>()).add(e);
            }

            reserva = new Reserva();
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en inicialización", e.getMessage()));
        }
    }

    public void guardar() {
        if (usuarioLogueado == null || usuarioLogueado.getId() == null) {
            showError("Error", "Debe iniciar sesión para reservar.");
            return;
        }

        if (idEspacio == null || diaSeleccionado == null || horarioSeleccionado == null) {
            showError("Error", "Debe completar todos los campos.");
            return;
        }

        // Calcular fecha y hora
        LocalDate fechaReserva = LocalDate.parse(diaSeleccionado);
        LocalTime horaInicio = getHoraInicioByHorario(horarioSeleccionado);
        if (horaInicio == null) {
            showError("Error", "Horario inválido");
            return;
        }
        LocalDateTime fechaHora = LocalDateTime.of(fechaReserva, horaInicio);
        reserva.setFecha(Date.from(fechaHora.atZone(ZoneId.systemDefault()).toInstant()));

        try {
            reservaService.guardarReserva(reserva, idEspacio, usuarioLogueado.getId());
            showInfo("Reserva guardada exitosamente");
            resetForm();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Ocurrió un problema al guardar la reserva");
        }
    }

    public void actualizarDiasDisponibles(AjaxBehaviorEvent event) {
        diasDisponibles.clear();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            diasDisponibles.add(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        }
        horariosDisponibles.clear();
    }

    public void actualizarHorariosDisponibles(AjaxBehaviorEvent event) {
        horariosDisponibles = obtenerHorariosDisponibles(idEspacio, diaSeleccionado);
    }

    private List<String> obtenerHorariosDisponibles(Long idEspacio, String dia) {
        try {
            return reservaService.obtenerHorariosDisponibles(idEspacio, dia);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "No se pudieron cargar los horarios");
            return new ArrayList<>();
        }
    }

    private LocalTime getHoraInicioByHorario(String horario) {
        switch (horario) {
            case "00:00 - 06:00": return LocalTime.of(0, 0);
            case "06:00 - 12:00": return LocalTime.of(6, 0);
            case "12:00 - 18:00": return LocalTime.of(12, 0);
            case "18:00 - 00:00": return LocalTime.of(18, 0);
            default: return null;
        }
    }

    private void resetForm() {
        reserva = new Reserva();
        idEspacio = null;
        diaSeleccionado = null;
        horarioSeleccionado = null;
        diasDisponibles.clear();
        horariosDisponibles.clear();
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

    public List<String> getDiasDisponibles() { return diasDisponibles; }
    public String getDiaSeleccionado() { return diaSeleccionado; }
    public void setDiaSeleccionado(String diaSeleccionado) { this.diaSeleccionado = diaSeleccionado; }

    public List<String> getHorariosDisponibles() { return horariosDisponibles; }
    public String getHorarioSeleccionado() { return horarioSeleccionado; }
    public void setHorarioSeleccionado(String horarioSeleccionado) { this.horarioSeleccionado = horarioSeleccionado; }

    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
}
