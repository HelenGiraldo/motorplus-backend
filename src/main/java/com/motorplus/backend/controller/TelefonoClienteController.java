package com.motorplus.backend.controller;

import com.motorplus.backend.dao.TelefonoClienteDAO;
import com.motorplus.backend.entity.TelefonoCliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{clienteId}/telefonos")
@CrossOrigin(origins = "*")
public class TelefonoClienteController {

    private TelefonoClienteDAO telefonoClienteDAO = new TelefonoClienteDAO();

    @GetMapping
    public List<TelefonoCliente> getTelefonosByCliente(@PathVariable Integer clienteId) {
        return telefonoClienteDAO.findByClienteId(clienteId);
    }

    @PostMapping
    public ResponseEntity<TelefonoCliente> addTelefono(@PathVariable Integer clienteId, @RequestBody TelefonoCliente telefono) {
        telefono.setIdCliente(clienteId);
        TelefonoCliente saved = telefonoClienteDAO.save(telefono);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idTelefono}")
    public ResponseEntity<Void> deleteTelefono(@PathVariable Integer clienteId, @PathVariable Integer idTelefono) {
        boolean success = telefonoClienteDAO.delete(idTelefono);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}