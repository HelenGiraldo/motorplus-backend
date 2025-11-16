package com.motorplus.backend.controller;

import com.motorplus.backend.dao.FacturaDAO;
import com.motorplus.backend.entity.Factura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    private FacturaDAO facturaDAO = new FacturaDAO();

    @GetMapping
    public List<Factura> getAll() {
        return facturaDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getById(@PathVariable Long id) {
        Factura factura = facturaDAO.findById(id);
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(factura);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("estado");
        if (newStatus == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean success = facturaDAO.updateStatus(id, newStatus);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}