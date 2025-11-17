package com.motorplus.backend.controller;

import com.motorplus.backend.dao.ServicioDAO;
import com.motorplus.backend.entity.Servicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    private ServicioDAO servicioDAO = new ServicioDAO();

    @GetMapping
    public List<Servicio> getAll() {
        return servicioDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getById(@PathVariable Long id) {
        Servicio servicio = servicioDAO.findById(id);
        return (servicio != null) ? ResponseEntity.ok(servicio) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Servicio create(@RequestBody Servicio servicio) {
        return servicioDAO.save(servicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> update(@PathVariable Long id, @RequestBody Servicio servicio) {
        Servicio updated = servicioDAO.update(id, servicio);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = servicioDAO.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}