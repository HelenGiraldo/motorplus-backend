package com.motorplus.backend.controller;

import com.motorplus.backend.dao.CorreoClienteDAO;
import com.motorplus.backend.entity.CorreoCliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{clienteId}/correos")
@CrossOrigin(origins = "*")
public class CorreoClienteController {

    private CorreoClienteDAO correoClienteDAO = new CorreoClienteDAO();

    @GetMapping
    public List<CorreoCliente> getCorreosByCliente(@PathVariable Integer clienteId) {
        return correoClienteDAO.findByClienteId(clienteId);
    }

    @PostMapping
    public ResponseEntity<CorreoCliente> addCorreo(@PathVariable Integer clienteId, @RequestBody CorreoCliente correo) {
        correo.setIdCliente(clienteId);
        CorreoCliente saved = correoClienteDAO.save(correo);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idCorreo}")
    public ResponseEntity<Void> deleteCorreo(@PathVariable Integer clienteId, @PathVariable Integer idCorreo) {
        boolean success = correoClienteDAO.delete(idCorreo);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}