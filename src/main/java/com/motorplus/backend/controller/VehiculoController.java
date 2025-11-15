package com.motorplus.backend.controller;

import com.motorplus.backend.dao.VehiculoDAO;
import com.motorplus.backend.entity.Vehiculo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {

    private VehiculoDAO vehiculoDAO = new VehiculoDAO();

    @GetMapping
    public List<Vehiculo> getAll() {
        return vehiculoDAO.findAll();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Vehiculo> getByClienteId(@PathVariable Long clienteId) {
        return vehiculoDAO.findByClienteId(clienteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> getById(@PathVariable Long id) {
        Vehiculo vehiculo = vehiculoDAO.findById(id);
        return (vehiculo != null) ? ResponseEntity.ok(vehiculo) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Vehiculo create(@RequestBody Vehiculo vehiculo) {
        return vehiculoDAO.save(vehiculo);
    }
}