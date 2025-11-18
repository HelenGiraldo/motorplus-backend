package com.motorplus.backend.controller;

import com.motorplus.backend.dao.DireccionMecanicoDAO;
import com.motorplus.backend.entity.DireccionMecanico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos/{mecanicoId}/direcciones")
@CrossOrigin(origins = "*")
public class DireccionMecanicoController {

    private DireccionMecanicoDAO direccionMecanicoDAO = new DireccionMecanicoDAO();

    @GetMapping
    public List<DireccionMecanico> getDireccionesByMecanico(@PathVariable Long mecanicoId) {
        return direccionMecanicoDAO.findByMecanicoId(mecanicoId);
    }

    @PostMapping
    public ResponseEntity<DireccionMecanico> addDireccion(@PathVariable Long mecanicoId, @RequestBody DireccionMecanico direccion) {
        direccion.setIdMecanico(mecanicoId);
        DireccionMecanico saved = direccionMecanicoDAO.save(direccion);
        return (saved != null) ? ResponseEntity.ok(saved) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> deleteDireccion(@PathVariable Long mecanicoId, @PathVariable Integer idDireccion) {
        boolean success = direccionMecanicoDAO.delete(idDireccion);
        return (success) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}