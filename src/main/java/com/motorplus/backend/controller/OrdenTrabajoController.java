package com.motorplus.backend.controller;

import com.motorplus.backend.entity.OrdenTrabajo;
import com.motorplus.backend.repository.OrdenTrabajoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    @Autowired
    private OrdenTrabajoRepository repository;

    @GetMapping
    public List<OrdenTrabajo> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajo> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public OrdenTrabajo create(@RequestBody OrdenTrabajo ordenTrabajo) {
        return repository.save(ordenTrabajo);
    }
}