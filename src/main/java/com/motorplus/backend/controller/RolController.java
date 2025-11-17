package com.motorplus.backend.controller;

import com.motorplus.backend.entity.Rol;
import com.motorplus.backend.service.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RolController {

    private RolService rolService = new RolService();

    @GetMapping
    public List<Rol> getAllRoles() {
        return rolService.obtenerTodosLosRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        Rol rol = rolService.obtenerRolPorId(id);
        if (rol == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<?> createRol(@RequestBody Rol rol) {
        try {
            boolean success = rolService.crearRol(rol);
            if (success) {
                return ResponseEntity.ok().body("Rol creado exitosamente");
            } else {
                return ResponseEntity.badRequest().body("Error al crear el rol");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}