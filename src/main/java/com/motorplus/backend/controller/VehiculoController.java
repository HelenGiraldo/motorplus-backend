package com.motorplus.backend.controller;

import com.motorplus.backend.dao.VehiculoDAO;
import com.motorplus.backend.entity.Vehiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
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

    // NUEVO ENDPOINT PARA CAMBIAR ESTADO
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        Boolean activo = request.get("activo");
        if (activo == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean success = dao.cambiarEstado(id, activo);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}