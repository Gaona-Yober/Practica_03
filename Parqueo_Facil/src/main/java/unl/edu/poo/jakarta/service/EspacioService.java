package unl.edu.poo.jakarta.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.poo.jakarta.dao.EspacioDAO;
import unl.edu.poo.jakarta.modelo.Espacio;

import java.util.List;

@Stateless
public class EspacioService {

    @Inject
    private EspacioDAO espacioDAO;

    public List<Espacio> obtenerTodos() {
        return espacioDAO.obtenerTodos();
    }

    public Espacio obtenerPorId(Long id) {
        return espacioDAO.obtenerPorId(id);
    }
}
