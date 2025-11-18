//package com.motorplus.backend.controller;
//
//import com.motorplus.backend.entity.DetalleOrdenMecanico;
//import com.motorplus.backend.service.DetalleOrdenMecanicoService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/ordenes-trabajo")
//@CrossOrigin(origins = "http://localhost:4200")
//public class DetalleOrdenMecanicoController {
//
//    private DetalleOrdenMecanicoService detalleOrdenMecanicoService;
//
//    public DetalleOrdenMecanicoController() {
//        this.detalleOrdenMecanicoService = new DetalleOrdenMecanicoService();
//    }
//
//    @GetMapping("/{ordenId}/mecanicos")
//    public ResponseEntity<List<DetalleOrdenMecanico>> getMecanicosDeOrden(@PathVariable Long ordenId) {
//        try {
//            List<DetalleOrdenMecanico> mecanicos = detalleOrdenMecanicoService.obtenerMecanicosPorOrden(ordenId);
//            return ResponseEntity.ok(mecanicos);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @PostMapping("/{ordenId}/mecanicos")
//    public ResponseEntity<Map<String, String>> asignarMecanico(@PathVariable Long ordenId, @RequestBody DetalleOrdenMecanico detalle) {
//        try {
//            // Establecer el ID de la orden desde el path variable
//            detalle.setIdOrden(ordenId);
//
//            boolean asignado = detalleOrdenMecanicoService.asignarMecanicoAOrden(detalle);
//            if (asignado) {
//                Map<String, String> response = new HashMap<>();
//                response.put("message", "Mecánico asignado correctamente");
//                return ResponseEntity.ok(response);
//            }
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error al asignar mecánico");
//            return ResponseEntity.badRequest().body(errorResponse);
//
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error del servidor: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(errorResponse);
//        }
//    }
//
//    @PutMapping("/{ordenId}/mecanicos/{mecanicoId}/servicios/{servicioId}")
//    public ResponseEntity<Map<String, String>> actualizarHoras(
//            @PathVariable Long ordenId,
//            @PathVariable Long mecanicoId,
//            @PathVariable Long servicioId,
//            @RequestBody DetalleOrdenMecanico detalle) {
//
//        try {
//            boolean actualizado = detalleOrdenMecanicoService.actualizarHorasTrabajadas(
//                    ordenId, mecanicoId, servicioId,
//                    detalle.getHorasTrabajadas(), detalle.getManoDeObra()
//            );
//
//            if (actualizado) {
//                Map<String, String> response = new HashMap<>();
//                response.put("message", "Horas actualizadas correctamente");
//                return ResponseEntity.ok(response);
//            }
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error al actualizar horas");
//            return ResponseEntity.badRequest().body(errorResponse);
//
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error del servidor: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(errorResponse);
//        }
//    }
//
//
//
//    @DeleteMapping("/{ordenId}/mecanicos/{mecanicoId}/servicios/{servicioId}")
//    public ResponseEntity<Map<String, String>> eliminarAsignacion(
//            @PathVariable Long ordenId,
//            @PathVariable Long mecanicoId,
//            @PathVariable Long servicioId) {
//
//        try {
//            boolean eliminado = detalleOrdenMecanicoService.eliminarAsignacionMecanico(ordenId, mecanicoId, servicioId);
//            if (eliminado) {
//                Map<String, String> response = new HashMap<>();
//                response.put("message", "Asignación eliminada correctamente");
//                return ResponseEntity.ok(response);
//            }
//
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error al eliminar asignación");
//            return ResponseEntity.badRequest().body(errorResponse);
//
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Error del servidor: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(errorResponse);
//        }
//    }
//}