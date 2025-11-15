package com.motorplus.backend.controller;

import com.motorplus.backend.entity.Repuesto;
import com.motorplus.backend.repository.RepuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repuestos")
@CrossOrigin(origins = "*")
public class RepuestoController {

    @Autowired
    private RepuestoRepository repository;

    @GetMapping
    public List<Repuesto> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repuesto> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Repuesto create(@RequestBody Repuesto repuesto) {
        return repository.save(repuesto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repuesto> update(@PathVariable Long id, @RequestBody Repuesto details) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setNombre(details.getNombre());
                    existing.setDescripcion(details.getDescripcion());
                    existing.setCostoUnitario(details.getCostoUnitario());
                    existing.setStockDisponible(details.getStockDisponible());
                    existing.setProveedor(details.getProveedor());
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