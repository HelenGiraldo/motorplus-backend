package com.motorplus.backend.controller;

import com.motorplus.backend.dao.DireccionProveedorDAO;
import com.motorplus.backend.entity.DireccionProveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores/{proveedorId}/direcciones")
@CrossOrigin(origins = "*")
public class DireccionProveedorController {

    private DireccionProveedorDAO direccionProveedorDAO = new DireccionProveedorDAO();

    @GetMapping
    public List<DireccionProveedor> getDireccionesByProveedor(@PathVariable Integer proveedorId) {
        return direccionProveedorDAO.findByProveedorId(proveedorId);
    }

    @PostMapping
    public ResponseEntity<DireccionProveedor> addDireccion(@PathVariable Integer proveedorId, @RequestBody DireccionProveedor direccion) {
        direccion.setIdProveedor(proveedorId);
        DireccionProveedor saved = direccionProveedorDAO.save(direccion);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable Integer proveedorId, @PathVariable Integer idDireccion) {
        boolean success = direccionProveedorDAO.delete(idDireccion);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}