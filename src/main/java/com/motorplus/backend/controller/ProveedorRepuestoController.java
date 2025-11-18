package com.motorplus.backend.controller;

import com.motorplus.backend.dao.ProveedorRepuestoDAO;
import com.motorplus.backend.entity.ProveedorRepuesto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedor-repuestos")
@CrossOrigin(origins = "*")
public class ProveedorRepuestoController {

    private ProveedorRepuestoDAO dao = new ProveedorRepuestoDAO();

    @GetMapping
    public List<ProveedorRepuesto> getAll() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorRepuesto> getById(@PathVariable Long id) {
        ProveedorRepuesto proveedorRepuesto = dao.findById(id);
        return (proveedorRepuesto != null) ? ResponseEntity.ok(proveedorRepuesto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/proveedor/{idProveedor}")
    public List<ProveedorRepuesto> getByProveedor(@PathVariable Integer idProveedor) {
        return dao.findByProveedorId(idProveedor);
    }

    @GetMapping("/repuesto/{idRepuesto}")
    public List<ProveedorRepuesto> getByRepuesto(@PathVariable Long idRepuesto) {
        return dao.findByRepuestoId(idRepuesto);
    }

    @PostMapping
    public ResponseEntity<ProveedorRepuesto> create(@RequestBody ProveedorRepuesto proveedorRepuesto) {
        // Validar que no exista ya la relación
        if (dao.existsByProveedorAndRepuesto(proveedorRepuesto.getIdProveedor(), proveedorRepuesto.getIdRepuesto())) {
            return ResponseEntity.badRequest().body(null);
        }

        ProveedorRepuesto saved = dao.save(proveedorRepuesto);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorRepuesto> update(@PathVariable Long id, @RequestBody ProveedorRepuesto details) {
        ProveedorRepuesto updated = dao.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = dao.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}