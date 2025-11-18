package com.motorplus.backend.controller;

import com.motorplus.backend.dao.CorreoProveedorDAO;
import com.motorplus.backend.entity.CorreoProveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores/{proveedorId}/correos")
@CrossOrigin(origins = "*")
public class CorreoProveedorController {

    private CorreoProveedorDAO correoProveedorDAO = new CorreoProveedorDAO();

    @GetMapping
    public List<CorreoProveedor> getCorreosByProveedor(@PathVariable Integer proveedorId) {
        return correoProveedorDAO.findByProveedorId(proveedorId);
    }

    @PostMapping
    public ResponseEntity<CorreoProveedor> addCorreo(@PathVariable Integer proveedorId, @RequestBody CorreoProveedor correo) {
        correo.setIdProveedor(proveedorId);
        CorreoProveedor saved = correoProveedorDAO.save(correo);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idCorreo}")
    public ResponseEntity<Void> deleteCorreo(@PathVariable Integer proveedorId, @PathVariable Integer idCorreo) {
        boolean success = correoProveedorDAO.delete(idCorreo);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}