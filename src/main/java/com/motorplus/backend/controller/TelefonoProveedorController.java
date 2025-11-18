package com.motorplus.backend.controller;

import com.motorplus.backend.dao.TelefonoProveedorDAO;
import com.motorplus.backend.entity.TelefonoProveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores/{proveedorId}/telefonos")
@CrossOrigin(origins = "*")
public class TelefonoProveedorController {

    private TelefonoProveedorDAO telefonoProveedorDAO = new TelefonoProveedorDAO();

    @GetMapping
    public List<TelefonoProveedor> getTelefonosByProveedor(@PathVariable Integer proveedorId) {
        return telefonoProveedorDAO.findByProveedorId(proveedorId);
    }

    @PostMapping
    public ResponseEntity<TelefonoProveedor> addTelefono(@PathVariable Integer proveedorId, @RequestBody TelefonoProveedor telefono) {
        telefono.setIdProveedor(proveedorId);
        TelefonoProveedor saved = telefonoProveedorDAO.save(telefono);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idTelefono}")
    public ResponseEntity<Void> deleteTelefono(@PathVariable Integer proveedorId, @PathVariable Integer idTelefono) {
        boolean success = telefonoProveedorDAO.delete(idTelefono);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
