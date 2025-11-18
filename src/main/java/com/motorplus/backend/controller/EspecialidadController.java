package com.motorplus.backend.controller;

import com.motorplus.backend.entity.Especialidad;
import com.motorplus.backend.service.EspecialidadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "*")
public class EspecialidadController {

    private EspecialidadService especialidadService = new EspecialidadService();

    @GetMapping
    public List<Especialidad> getAllEspecialidades() {
        return especialidadService.obtenerTodasLasEspecialidades();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Especialidad> getEspecialidadById(@PathVariable Long id) {
        Especialidad especialidad = especialidadService.obtenerEspecialidadPorId(id);
        if (especialidad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(especialidad);
    }

    @PostMapping
    public ResponseEntity<?> createEspecialidad(@RequestBody Especialidad especialidad) {
        try {
            boolean success = especialidadService.crearEspecialidad(especialidad);
            if (success) {
                return ResponseEntity.ok().body("Especialidad creada exitosamente");
            } else {
                return ResponseEntity.badRequest().body("Error al crear la especialidad");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}