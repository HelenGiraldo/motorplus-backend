package com.motorplus.backend.controller;

import com.motorplus.backend.dao.*;
import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.*;
import com.motorplus.backend.service.DetalleOrdenServicioService;
import com.motorplus.backend.service.FacturaPDFService;
import com.motorplus.backend.service.OrdenTrabajoService;
import com.motorplus.backend.service.RolService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
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
    private DetalleOrdenMecanicoDAO detalleOrdenMecanicoDAO = new DetalleOrdenMecanicoDAO();
    private FacturaDAO facturaDAO = new FacturaDAO();
    private FacturaPDFService facturaPDFService = new FacturaPDFService();

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


    // EN OrdenTrabajoController.java - MODIFICAR SOLO ESTE MÉTODO
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

            if (cantidad <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "La cantidad de repuestos debe ser mayor a cero."));
            }

            // CAMBIO: Usar ProveedorRepuestoDAO en lugar de RepuestoDAO
            ProveedorRepuestoDAO proveedorRepuestoDAO = new ProveedorRepuestoDAO();

            // Verificar stock en ProveedorRepuesto
            boolean stockSuficiente = proveedorRepuestoDAO.verificarStockDisponible(repuestoId, cantidad);

            if (!stockSuficiente) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Stock insuficiente en proveedores para el repuesto ID: " + repuestoId));
            }

            // Obtener precio del repuesto (puedes usar el de RepuestoDAO como antes)
            Repuesto repuesto = repuestoDAO.findById(repuestoId);
            if (repuesto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Repuesto no encontrado"));
            }

            // ✅ CAMBIO: Restar stock de ProveedorRepuesto
            boolean stockActualizado = proveedorRepuestoDAO.restarStockRepuesto(repuestoId, cantidad);
            if (!stockActualizado) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al descontar stock del repuesto en proveedores."));
            }

            // El resto del código queda IGUAL
            boolean success = ordenRepuestoDAO.save(id, repuestoId, cantidad, repuesto.getPrecioVenta());

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Repuesto agregado y stock actualizado correctamente"));
            } else {
                // Si falla, devolver el stock
                proveedorRepuestoDAO.restarStockRepuesto(repuestoId, -cantidad); // Sumar para devolver
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al agregar repuesto a la orden. Stock revertido."));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Error en los datos: " + e.getMessage()));
        }
    }

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

    @PostMapping("/{id}/facturar")
    public ResponseEntity<Map<String, Object>> facturarOrden(@PathVariable Long id) {
        try {
            System.out.println("🔧 Facturando orden ID: " + id);

            OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
            if (orden == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Orden no encontrada"));
            }

            // Calcular totales
            Double totalServicios = calcularTotalServicios(id);
            Double totalRepuestos = calcularTotalRepuestos(id);
            Double totalManoObra = calcularTotalManoObra(id);

            Double subtotal = totalServicios + totalRepuestos + totalManoObra;
            Double iva = subtotal * 0.19; // 🔥 Cambié a 19% que es el IVA real en Colombia
            Double total = subtotal + iva;

            // CAMBIO: Estado "Facturada" para la orden, pero factura como "Pendiente"
            String estadoOrden = "Facturada";

            OrdenTrabajoService ordenTrabajoService = new OrdenTrabajoService();
            boolean facturada = ordenTrabajoService.facturarOrden(id, subtotal, iva, total, estadoOrden);

            if (facturada) {
                // Crear factura con estado PENDIENTE
                boolean facturaCreada = crearFactura(id, subtotal, iva, total, totalManoObra, totalRepuestos);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Factura generada correctamente. Estado: PENDIENTE DE PAGO");
                response.put("detalles", Map.of(
                        "servicios", totalServicios,
                        "repuestos", totalRepuestos,
                        "manoObra", totalManoObra,
                        "subtotal", subtotal,
                        "iva", iva,
                        "total", total,
                        "estadoFactura", "Pendiente" // 🔥 Informar el estado real
                ));

                if (facturaCreada) {
                    response.put("facturaCreada", true);
                }

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al generar factura"));
            }

        } catch (Exception e) {
            System.out.println(" ERROR en facturación: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error interno del servidor"));
        }
    }

    private boolean crearFactura(Long ordenId, Double subtotal, Double iva, Double total, Double manoObra, Double repuestos) {
        try {
            String sql = "INSERT INTO Factura (idOrdenTrabajo, fechaEmision, costoManoObra, costoRepuestos, impuestos, valorTotal, estadoPago) VALUES (?, NOW(), ?, ?, ?, ?, 'Pendiente')";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, ordenId);
                pstmt.setDouble(2, manoObra);
                pstmt.setDouble(3, repuestos);
                pstmt.setDouble(4, iva);
                pstmt.setDouble(5, total);

                int result = pstmt.executeUpdate();
                System.out.println("Factura PENDIENTE creada para orden: " + ordenId + " - Filas afectadas: " + result);
                return result > 0;
            }
        } catch (Exception e) {
            System.out.println("Error creando factura: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Double calcularTotalServicios(Long ordenId) {
        try {
            List<DetalleOrdenServicio> servicios = detalleOrdenServicioService.obtenerServiciosPorOrden(ordenId);
            return servicios.stream()
                    .mapToDouble(servicio -> servicio.getPrecioBase() != null ? servicio.getPrecioBase() : 0.0)
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private Double calcularTotalRepuestos(Long ordenId) {
        try {
            List<OrdenRepuesto> repuestos = ordenRepuestoDAO.findByOrdenId(ordenId);
            return repuestos.stream()
                    .mapToDouble(repuesto -> {
                        // Calcular el costo total basado en cantidad y precio unitario
                        if (repuesto.getPrecioUnitario() != null && repuesto.getCantidad() != null) {
                            return repuesto.getPrecioUnitario().doubleValue() * repuesto.getCantidad();
                        }
                        return 0.0;
                    })
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private Double calcularTotalManoObra(Long ordenId) {
        try {
            List<DetalleOrdenMecanico> mecanicos = detalleOrdenMecanicoDAO.findByOrdenId(ordenId);
            return mecanicos.stream()
                    .mapToDouble(mecanico -> mecanico.getManoDeObra() != null ? mecanico.getManoDeObra() : 0.0)
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // --- MECÁNICOS --- (Agregar en OrdenTrabajoController.java)
    @GetMapping("/{id}/mecanicos")
    public ResponseEntity<?> getMecanicosDeOrden(@PathVariable Long id) {
        OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
        if (orden == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Orden no encontrada"));
        }

        if (isOrdenFinalizada(orden.getEstado())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "No se pueden consultar mecánicos de una orden " + orden.getEstado()));
        }

        List<DetalleOrdenMecanico> mecanicos = detalleOrdenMecanicoDAO.findByOrdenId(id);
        return ResponseEntity.ok(mecanicos);
    }

    @PostMapping("/{id}/mecanicos")
    public ResponseEntity<Map<String, Object>> asignarMecanicoAOrden(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            System.out.println(" Asignando mecánico a orden: " + id);
            System.out.println(" Datos recibidos: " + body);

            OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
            if (orden == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Orden no encontrada"));
            }

            if (isOrdenFinalizada(orden.getEstado())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "No se pueden asignar mecánicos a una orden " + orden.getEstado()));
            }

            // Validar campos requeridos
            if (!body.containsKey("idMecanico") || !body.containsKey("idServicio") || !body.containsKey("horasTrabajadas")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Faltan campos requeridos: idMecanico, idServicio, horasTrabajadas"));
            }

            Long idMecanico = Long.valueOf(body.get("idMecanico").toString());
            Long idServicio = Long.valueOf(body.get("idServicio").toString());
            Integer horasTrabajadas = Integer.valueOf(body.get("horasTrabajadas").toString());

            // Verificar si ya existe esta asignación
            if (detalleOrdenMecanicoDAO.existeAsignacion(id, idMecanico, idServicio)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message",
                                "Este mecánico ya está asignado a este servicio en la orden"));
            }

            // Validar que el mecánico y servicio existan
            MecanicoDAO mecanicoDAO = new MecanicoDAO();
            ServicioDAO servicioDAO = new ServicioDAO();

            if (mecanicoDAO.findById(idMecanico) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Mecánico no encontrado"));
            }

            if (servicioDAO.findById(idServicio) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Servicio no encontrado"));
            }

            // Crear detalle
            DetalleOrdenMecanico detalle = new DetalleOrdenMecanico();
            detalle.setIdOrden(id);
            detalle.setIdMecanico(idMecanico);
            detalle.setIdServicio(idServicio);
            detalle.setHorasTrabajadas(horasTrabajadas);

            // Opcional: idRol
            if (body.containsKey("idRol") && body.get("idRol") != null) {
                Long idRol = Long.valueOf(body.get("idRol").toString());
                detalle.setIdRol(idRol);
            }

            boolean success = detalleOrdenMecanicoDAO.asignarMecanico(detalle);

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Mecánico asignado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message",
                                "No se pudo asignar el mecánico. Puede que ya esté asignado a este servicio."));
            }

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Error en el formato de los datos numéricos"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/mecanicos/{mecanicoId}/servicios/{servicioId}")
    public ResponseEntity<Map<String, Object>> actualizarHorasMecanico(
            @PathVariable Long id,
            @PathVariable Long mecanicoId,
            @PathVariable Long servicioId,
            @RequestBody Map<String, Object> body) {

        try {
            OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
            if (orden == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Orden no encontrada"));
            }

            if (isOrdenFinalizada(orden.getEstado())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "No se pueden actualizar mecánicos de una orden " + orden.getEstado()));
            }

            if (!body.containsKey("horasTrabajadas")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Falta el campo: horasTrabajadas"));
            }

            Integer horasTrabajadas = Integer.valueOf(body.get("horasTrabajadas").toString());

            boolean success = detalleOrdenMecanicoDAO.actualizarHorasYManoObra(id, mecanicoId, servicioId, horasTrabajadas, null);

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Horas actualizadas correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al actualizar horas"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/mecanicos/{mecanicoId}/servicios/{servicioId}")
    public ResponseEntity<Map<String, Object>> eliminarMecanicoDeOrden(
            @PathVariable Long id,
            @PathVariable Long mecanicoId,
            @PathVariable Long servicioId) {

        try {
            OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
            if (orden == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Orden no encontrada"));
            }

            if (isOrdenFinalizada(orden.getEstado())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "No se pueden eliminar mecánicos de una orden " + orden.getEstado()));
            }

            boolean success = detalleOrdenMecanicoDAO.eliminarAsignacion(id, mecanicoId, servicioId);

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Mecánico eliminado de la orden"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Error al eliminar mecánico"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/factura/pdf")
    public ResponseEntity<byte[]> descargarFacturaPorOrden(@PathVariable Long id) {
        try {
            // Buscar la factura asociada a esta orden
            Factura factura = facturaDAO.findByOrdenId(id);

            if (factura == null) {
                return ResponseEntity.notFound().build();
            }


            byte[] pdfBytes = facturaPDFService.generarFacturaPDF(factura);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "factura_orden_" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}