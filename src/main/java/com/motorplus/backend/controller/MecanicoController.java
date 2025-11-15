package com.motorplus.backend.controller;

import com.motorplus.backend.entity.Mecanico;
import com.motorplus.backend.repository.MecanicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@CrossOrigin(origins = "*")
public class MecanicoController {

    @Autowired
    private MecanicoRepository repository;

    @GetMapping
    public List<Mecanico> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mecanico> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mecanico create(@RequestBody Mecanico mecanico) {
        return repository.save(mecanico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mecanico> update(@PathVariable Long id, @RequestBody Mecanico details) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setNombres(details.getNombres());
                    existing.setApellidos(details.getApellidos());
                    existing.setDocumento(details.getDocumento());
                    existing.setEspecialidad(details.getEspecialidad());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}