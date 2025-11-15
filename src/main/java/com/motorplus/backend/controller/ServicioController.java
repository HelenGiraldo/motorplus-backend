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

    private ServicioDAO dao = new ServicioDAO();

    @GetMapping
    public List<Servicio> getAll() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getById(@PathVariable Long id) {
        Servicio servicio = dao.findById(id);
        return (servicio != null) ? ResponseEntity.ok(servicio) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Servicio create(@RequestBody Servicio servicio) {
        return dao.save(servicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> update(@PathVariable Long id, @RequestBody Servicio details) {
        Servicio updated = dao.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = dao.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}