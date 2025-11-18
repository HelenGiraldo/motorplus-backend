package com.motorplus.backend.controller;

import com.motorplus.backend.dao.TelefonoMecanicoDAO;
import com.motorplus.backend.entity.TelefonoMecanico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos/{mecanicoId}/telefonos")
@CrossOrigin(origins = "*")
public class TelefonoMecanicoController {

    private TelefonoMecanicoDAO telefonoMecanicoDAO = new TelefonoMecanicoDAO();

    @GetMapping
    public List<TelefonoMecanico> getTelefonosByMecanico(@PathVariable Long mecanicoId) {
        return telefonoMecanicoDAO.findByMecanicoId(mecanicoId);
    }

    @PostMapping
    public ResponseEntity<TelefonoMecanico> addTelefono(@PathVariable Long mecanicoId, @RequestBody TelefonoMecanico telefono) {
        telefono.setIdMecanico(mecanicoId);
        // No necesitamos recibir esPrincipal desde el frontend
        // El DAO lo manejará automáticamente
        TelefonoMecanico saved = telefonoMecanicoDAO.save(telefono);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idTelefono}")
    public ResponseEntity<Void> deleteTelefono(@PathVariable Long mecanicoId, @PathVariable Integer idTelefono) {
        boolean success = telefonoMecanicoDAO.delete(idTelefono);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}