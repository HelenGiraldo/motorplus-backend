package com.motorplus.backend.service;

import com.motorplus.backend.dao.RolDAO;
import com.motorplus.backend.entity.Rol;

import java.util.List;

public class RolService {

    private RolDAO rolDAO;

    public RolService() {
        this.rolDAO = new RolDAO();
    }

    public List<Rol> obtenerTodosLosRoles() {
        return rolDAO.findAll();
    }

    public Rol obtenerRolPorId(Long id) {
        return rolDAO.findById(id);
    }

    public Rol obtenerRolPorNombre(String nombre) {
        return rolDAO.findByNombre(nombre);
    }

    public boolean crearRol(Rol rol) {
        // Validar que no exista un rol con el mismo nombre
        Rol existente = rolDAO.findByNombre(rol.getNombre());
        if (existente != null) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rol.getNombre());
        }

        return rolDAO.save(rol);
    }
}