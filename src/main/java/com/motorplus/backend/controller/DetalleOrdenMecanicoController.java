package com.motorplus.backend.controller;

import com.motorplus.backend.entity.DetalleOrdenMecanico;
import com.motorplus.backend.service.DetalleOrdenMecanicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "http://localhost:4200")
public class DetalleOrdenMecanicoController {

    private DetalleOrdenMecanicoService detalleOrdenMecanicoService;

    public DetalleOrdenMecanicoController() {
        this.detalleOrdenMecanicoService = new DetalleOrdenMecanicoService();
    }

    @GetMapping("/{ordenId}/mecanicos")
    public ResponseEntity<List<DetalleOrdenMecanico>> getMecanicosDeOrden(@PathVariable Long ordenId) {
        try {
            List<DetalleOrdenMecanico> mecanicos = detalleOrdenMecanicoService.obtenerMecanicosPorOrden(ordenId);
            return ResponseEntity.ok(mecanicos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{ordenId}/mecanicos")
    public ResponseEntity<String> asignarMecanico(@PathVariable Long ordenId, @RequestBody DetalleOrdenMecanico detalle) {
        try {
            // Establecer el ID de la orden desde el path variable
            detalle.setIdOrden(ordenId);

            boolean asignado = detalleOrdenMecanicoService.asignarMecanicoAOrden(detalle);
            if (asignado) {
                return ResponseEntity.ok("Mecánico asignado correctamente");
            }
            return ResponseEntity.badRequest().body("Error al asignar mecánico");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
        }
    }

    @PutMapping("/{ordenId}/mecanicos/{mecanicoId}/servicios/{servicioId}")
    public ResponseEntity<String> actualizarHoras(
            @PathVariable Long ordenId,
            @PathVariable Long mecanicoId,
            @PathVariable Long servicioId,
            @RequestBody DetalleOrdenMecanico detalle) {

        try {
            boolean actualizado = detalleOrdenMecanicoService.actualizarHorasTrabajadas(
                    ordenId, mecanicoId, servicioId,
                    detalle.getHorasTrabajadas(), detalle.getManoDeObra()
            );

            if (actualizado) {
                return ResponseEntity.ok("Horas actualizadas correctamente");
            }
            return ResponseEntity.badRequest().body("Error al actualizar horas");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{ordenId}/mecanicos/{mecanicoId}/servicios/{servicioId}")
    public ResponseEntity<String> eliminarAsignacion(
            @PathVariable Long ordenId,
            @PathVariable Long mecanicoId,
            @PathVariable Long servicioId) {

        try {
            boolean eliminado = detalleOrdenMecanicoService.eliminarAsignacionMecanico(ordenId, mecanicoId, servicioId);
            if (eliminado) {
                return ResponseEntity.ok("Asignación eliminada correctamente");
            }
            return ResponseEntity.badRequest().body("Error al eliminar asignación");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
        }
    }
}