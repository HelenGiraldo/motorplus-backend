package com.motorplus.backend.controller;

import com.motorplus.backend.entity.Vehiculo;
import com.motorplus.backend.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    @Autowired
    private VehiculoRepository repository;

    @GetMapping
    public List<Vehiculo> getAll() {
        return repository.findAll();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Vehiculo> getByClienteId(@PathVariable Long clienteId) {
        return repository.findByClienteIdCliente(clienteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Vehiculo create(@RequestBody Vehiculo vehiculo) {
        return repository.save(vehiculo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> update(@PathVariable Long id, @RequestBody Vehiculo details) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setPlaca(details.getPlaca());
                    existing.setMarca(details.getMarca());
                    existing.setModelo(details.getModelo());
                    existing.setAnio(details.getAnio());
                    existing.setTipoServicioRequerido(details.getTipoServicioRequerido());
                    existing.setCliente(details.getCliente());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}