package com.motorplus.backend.controller;

import com.motorplus.backend.dao.DireccionClienteDAO;
import com.motorplus.backend.entity.DireccionCliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{clienteId}/direcciones")
@CrossOrigin(origins = "*")
public class DireccionClienteController {

    private DireccionClienteDAO direccionClienteDAO = new DireccionClienteDAO();

    @GetMapping
    public List<DireccionCliente> getDireccionesByCliente(@PathVariable Integer clienteId) {
        return direccionClienteDAO.findByClienteId(clienteId);
    }

    @PostMapping
    public ResponseEntity<DireccionCliente> addDireccion(@PathVariable Integer clienteId, @RequestBody DireccionCliente direccion) {
        direccion.setIdCliente(clienteId);
        DireccionCliente saved = direccionClienteDAO.save(direccion);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable Integer clienteId, @PathVariable Integer idDireccion) {
        boolean success = direccionClienteDAO.delete(idDireccion);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}