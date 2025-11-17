package com.motorplus.backend.controller;

import com.motorplus.backend.dao.*;
import com.motorplus.backend.entity.*;
import com.motorplus.backend.service.DetalleOrdenServicioService;
import com.motorplus.backend.service.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    private OrdenTrabajoDAO ordenTrabajoDAO = new OrdenTrabajoDAO();
    private DetalleOrdenServicioService detalleOrdenServicioService = new DetalleOrdenServicioService();
    private OrdenRepuestoDAO ordenRepuestoDAO = new OrdenRepuestoDAO();
    private RepuestoDAO repuestoDAO = new RepuestoDAO();
    private ServicioDAO servicioDAO = new ServicioDAO();

    @GetMapping
    public List<OrdenTrabajo> getAll() {
        return ordenTrabajoDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
        if (orden == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Orden no encontrada"));
        }

        if (isOrdenFinalizada(orden.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "No se puede acceder a esta orden porque está " + orden.getEstado().toLowerCase(),
                            "estado", orden.getEstado(),
                            "bloqueada", true
                    ));
        }

        return ResponseEntity.ok(orden);
    }

    @PostMapping
    public OrdenTrabajo create(@RequestBody OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoDAO.save(ordenTrabajo);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("estado");
        if (newStatus == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean success = ordenTrabajoDAO.updateStatus(id, newStatus);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // --- SERVICIOS ---
    @GetMapping("/{id}/servicios")
    public ResponseEntity<?> getServiciosDeOrden(@PathVariable Long id) {
        OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
        if (orden == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Orden no encontrada"));
        }

        if (isOrdenFinalizada(orden.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "No se pueden consultar servicios de una orden " + orden.getEstado()));
        }

        List<DetalleOrdenServicio> servicios = detalleOrdenServicioService.obtenerServiciosPorOrden(id);
        return ResponseEntity.ok(servicios);
    }

    @PostMapping("/{id}/servicios")
    public ResponseEntity<Map<String, Object>> addServicioAOrden(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
            if (orden == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Orden no encontrada"));
            }

            if (isOrdenFinalizada(orden.getEstado())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "No se pueden agregar servicios a una orden " + orden.getEstado()));
            }

            Long servicioId = Long.valueOf(body.get("idServicio").toString());

            // Crear DetalleOrdenServicio
            DetalleOrdenServicio detalle = new DetalleOrdenServicio();
            detalle.setIdOrden(id);
            detalle.setIdServicio(servicioId);

            // Opcional: descripción del trabajo
            if (body.containsKey("descripcionTrabajo")) {
                detalle.setDescripcionTrabajo(body.get("descripcionTrabajo").toString());
            }

            boolean success = detalleOrdenServicioService.agregarServicioAOrden(detalle);

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Servicio agregado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al agregar servicio"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    // --- REPUESTOS ---
    @GetMapping("/{id}/repuestos")
    public ResponseEntity<?> getRepuestosDeOrden(@PathVariable Long id) {
        OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
        if (orden == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Orden no encontrada"));
        }

        if (isOrdenFinalizada(orden.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "No se pueden consultar repuestos de una orden " + orden.getEstado()));
        }

        return ResponseEntity.ok(ordenRepuestoDAO.findByOrdenId(id));
    }

    @PostMapping("/{id}/repuestos")
    public ResponseEntity<Map<String, Object>> addRepuestoAOrden(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
            if (orden == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Orden no encontrada"));
            }

            if (isOrdenFinalizada(orden.getEstado())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "No se pueden agregar repuestos a una orden " + orden.getEstado()));
            }

            Long repuestoId = Long.valueOf(body.get("idRepuesto").toString());
            Integer cantidad = Integer.valueOf(body.get("cantidad").toString());

            Repuesto repuesto = repuestoDAO.findById(repuestoId);
            if (repuesto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Repuesto no encontrado"));
            }

            boolean success = ordenRepuestoDAO.save(id, repuestoId, cantidad, repuesto.getCostoUnitario());

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Repuesto agregado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al agregar repuesto"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Error en los datos: " + e.getMessage()));
        }
    }

    // --- MÉTODOS PARA LOS CATÁLOGOS ---
    @GetMapping("/catalogos/servicios")
    public List<Servicio> getCatalogoServicios() {
        return servicioDAO.findAll();
    }

    @GetMapping("/catalogos/repuestos")
    public List<Repuesto> getCatalogoRepuestos() {
        return repuestoDAO.findAll();
    }

    // --- MÉTODO AUXILIAR ---
    private boolean isOrdenFinalizada(String estado) {
        return "Facturada".equalsIgnoreCase(estado) ||
                "Cancelada".equalsIgnoreCase(estado) ||
                "Cerrada".equalsIgnoreCase(estado) ||
                "Finalizada".equalsIgnoreCase(estado) ||
                "Completada".equalsIgnoreCase(estado);
    }

    @GetMapping("/catalogos/roles")
    public List<Rol> getCatalogoRoles() {
        RolService rolService = new RolService();
        return rolService.obtenerTodosLosRoles();
    }
}