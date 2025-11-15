package com.motorplus.backend.controller;

import com.motorplus.backend.dao.OrdenTrabajoDAO;
import com.motorplus.backend.entity.OrdenTrabajo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    private OrdenTrabajoDAO ordenTrabajoDAO = new OrdenTrabajoDAO();

    @GetMapping
    public List<OrdenTrabajo> getAll() {
        return ordenTrabajoDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajo> getById(@PathVariable Long id) {
        OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
        return (orden != null) ? ResponseEntity.ok(orden) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public OrdenTrabajo create(@RequestBody OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoDAO.save(ordenTrabajo);
    }
}