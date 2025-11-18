package com.motorplus.backend.controller;

import com.motorplus.backend.dao.CorreoMecanicoDAO;
import com.motorplus.backend.entity.CorreoMecanico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos/{mecanicoId}/correos")
@CrossOrigin(origins = "*")
public class CorreoMecanicoController {

    private CorreoMecanicoDAO correoMecanicoDAO = new CorreoMecanicoDAO();

    @GetMapping
    public List<CorreoMecanico> getCorreosByMecanico(@PathVariable Long mecanicoId) {
        return correoMecanicoDAO.findByMecanicoId(mecanicoId);
    }

    @PostMapping
    public ResponseEntity<CorreoMecanico> addCorreo(@PathVariable Long mecanicoId, @RequestBody CorreoMecanico correo) {
        correo.setIdMecanico(mecanicoId);
        // No necesitamos recibir esPrincipal desde el frontend
        // El DAO lo manejará automáticamente
        CorreoMecanico saved = correoMecanicoDAO.save(correo);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idCorreo}")
    public ResponseEntity<Void> deleteCorreo(@PathVariable Long mecanicoId, @PathVariable Integer idCorreo) {
        boolean success = correoMecanicoDAO.delete(idCorreo);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}