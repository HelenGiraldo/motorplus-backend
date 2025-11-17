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

    private ClienteDAO dao = new ClienteDAO();

    @GetMapping
    public List<Cliente> getAll() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        Cliente cliente = dao.findById(id);
        return (cliente != null) ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Cliente create(@RequestBody Cliente cliente) {
        return dao.save(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @RequestBody Cliente details) {
        Cliente updated = dao.update(id, details);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = dao.deleteById(id);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}