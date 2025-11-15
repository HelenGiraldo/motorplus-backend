package com.motorplus.backend.controller;

import com.motorplus.backend.dao.ProveedorDAO;
import com.motorplus.backend.entity.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    private ProveedorDAO dao = new ProveedorDAO();

    @GetMapping
    public List<Proveedor> getAll() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getById(@PathVariable Long id) {
        Proveedor proveedor = dao.findById(id);
        return (proveedor != null) ? ResponseEntity.ok(proveedor) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Proveedor create(@RequestBody Proveedor proveedor) {
        return dao.save(proveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> update(@PathVariable Long id, @RequestBody Proveedor details) {
        Proveedor updated = dao.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = dao.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}