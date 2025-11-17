package com.motorplus.backend.controller;

import com.motorplus.backend.dao.VehiculoDAO;
import com.motorplus.backend.entity.Vehiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private VehiculoDAO dao = new VehiculoDAO();

    @GetMapping
    public List<Vehiculo> getAll() {
        return dao.findAll();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Vehiculo> getByClienteId(@PathVariable Long clienteId) {
        return dao.findByClienteId(clienteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> getById(@PathVariable Long id) {
        Vehiculo vehiculo = dao.findById(id);
        return (vehiculo != null) ? ResponseEntity.ok(vehiculo) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Vehiculo create(@RequestBody Vehiculo vehiculo) {
        return dao.save(vehiculo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> update(@PathVariable Long id, @RequestBody Vehiculo details) {
        Vehiculo updated = dao.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = dao.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/all-history")
    public List<Vehiculo> getAllHistory() {
        return dao.findAllIncludingInactive();
    }
}