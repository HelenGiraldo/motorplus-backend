package com.motorplus.backend.controller;

import com.motorplus.backend.dao.MecanicoDAO;
import com.motorplus.backend.entity.Mecanico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    private MecanicoDAO mecanicoDAO = new MecanicoDAO();

    @GetMapping
    public List<Mecanico> getAll() {
        return mecanicoDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mecanico> getById(@PathVariable Long id) {
        Mecanico mecanico = mecanicoDAO.findById(id);
        return (mecanico != null) ? ResponseEntity.ok(mecanico) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Mecanico create(@RequestBody Mecanico mecanico) {
        return mecanicoDAO.save(mecanico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mecanico> update(@PathVariable Long id, @RequestBody Mecanico details) {
        Mecanico updated = mecanicoDAO.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = mecanicoDAO.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}