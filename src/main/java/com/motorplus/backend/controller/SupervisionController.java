package com.motorplus.backend.controller;

import com.motorplus.backend.dao.SupervisionDAO;
import com.motorplus.backend.entity.Supervision;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/supervisiones")
@CrossOrigin(origins = "*")
public class SupervisionController {

    private SupervisionDAO supervisionDAO = new SupervisionDAO();

    @GetMapping
    public List<Supervision> getAll() {
        return supervisionDAO.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Supervision supervision) {
        boolean success = supervisionDAO.save(supervision);
        return success ? ResponseEntity.ok().body("Supervisión creada exitosamente") :
                ResponseEntity.badRequest().body("Error al crear supervisión");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean success = supervisionDAO.delete(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}