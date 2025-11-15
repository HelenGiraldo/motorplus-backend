package com.motorplus.backend.controller;

import com.motorplus.backend.dao.RepuestoDAO;
import com.motorplus.backend.entity.Repuesto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repuestos")
@CrossOrigin(origins = "*")
public class RepuestoController {

    private RepuestoDAO dao = new RepuestoDAO();

    @GetMapping
    public List<Repuesto> getAll() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repuesto> getById(@PathVariable Long id) {
        Repuesto repuesto = dao.findById(id);
        return (repuesto != null) ? ResponseEntity.ok(repuesto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Repuesto create(@RequestBody Repuesto repuesto) {
        return dao.save(repuesto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repuesto> update(@PathVariable Long id, @RequestBody Repuesto details) {
        Repuesto updated = dao.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = dao.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}