package unl.edu.poo.jakarta.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.poo.jakarta.modelo.Espacio;
import unl.edu.poo.jakarta.modelo.Reserva;
import unl.edu.poo.jakarta.modelo.Usuario;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Stateless
public class ReservaService {

    @PersistenceContext(unitName = "reservaPU")
    private EntityManager em;

    public void guardarReserva(Reserva reserva, Long idEspacio, Long idUsuario) {
        Usuario usuario = em.find(Usuario.class, idUsuario);
        Espacio espacio = em.find(Espacio.class, idEspacio);

        if (usuario == null || espacio == null) {
            throw new IllegalArgumentException("Usuario o Espacio no encontrado");
        }

        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);

        em.persist(reserva);
    }

    public List<String> obtenerHorariosDisponibles(Long idEspacio, String dia) {
        if (idEspacio == null || dia == null || dia.isEmpty()) {
            return Collections.emptyList();
        }

        Date fechaInicio = java.sql.Date.valueOf(LocalDate.parse(dia));
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicio);
        cal.add(Calendar.DATE, 1);
        Date fechaFin = cal.getTime();

        List<Reserva> reservas = em.createQuery(
                        "SELECT r FROM Reserva r WHERE r.espacio.id = :idEspacio AND r.fecha >= :inicio AND r.fecha < :fin",
                        Reserva.class)
                .setParameter("idEspacio", idEspacio)
                .setParameter("inicio", fechaInicio)
                .setParameter("fin", fechaFin)
                .getResultList();

        List<String> bloques = new ArrayList<>(Arrays.asList(
                "00:00 - 06:00", "06:00 - 12:00",
                "12:00 - 18:00", "18:00 - 00:00"
        ));

        for (Reserva r : reservas) {
            String bloqueOcupado = obtenerBloquePorHora(r.getFecha());
            bloques.remove(bloqueOcupado);
        }

        return bloques;
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
}
