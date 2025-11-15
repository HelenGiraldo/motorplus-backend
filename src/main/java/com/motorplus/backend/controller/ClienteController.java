package com.motorplus.backend.controller;

import com.motorplus.backend.dao.ClienteDAO;
import com.motorplus.backend.entity.Cliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private ClienteDAO clienteDAO = new ClienteDAO();

    @GetMapping
    public List<Cliente> getAll() {
        return clienteDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        Cliente cliente = clienteDAO.findById(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public Cliente create(@RequestBody Cliente cliente) {
        return clienteDAO.save(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @RequestBody Cliente details) {
        Cliente updatedCliente = clienteDAO.update(id, details);
        if (updatedCliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = clienteDAO.deleteById(id);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}