package com.motorplus.backend.service;

import com.motorplus.backend.dao.EspecialidadDAO;
import com.motorplus.backend.entity.Especialidad;

import java.util.List;

public class EspecialidadService {

    private EspecialidadDAO especialidadDAO;

    public EspecialidadService() {
        this.especialidadDAO = new EspecialidadDAO();
    }

    public List<Especialidad> obtenerTodasLasEspecialidades() {
        return especialidadDAO.findAll();
    }

    public Especialidad obtenerEspecialidadPorId(Long id) {
        return especialidadDAO.findById(id);
    }

    public Especialidad obtenerEspecialidadPorNombre(String nombre) {
        return especialidadDAO.findByNombre(nombre);
    }

    public boolean crearEspecialidad(Especialidad especialidad) {
        // Validar que no exista una especialidad con el mismo nombre
        Especialidad existente = especialidadDAO.findByNombre(especialidad.getNombre());
        if (existente != null) {
            throw new RuntimeException("Ya existe una especialidad con el nombre: " + especialidad.getNombre());
        }

        return especialidadDAO.save(especialidad);
    }
}
