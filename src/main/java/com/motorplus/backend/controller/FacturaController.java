package com.motorplus.backend.controller;

import com.motorplus.backend.dao.FacturaDAO;
import com.motorplus.backend.dao.OrdenTrabajoDAO;
import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Factura;
import com.motorplus.backend.service.FacturaPDFService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final OrdenTrabajoDAO ordenTrabajoDAO = new OrdenTrabajoDAO();
    private final FacturaPDFService facturaPDFService = new FacturaPDFService();

    // ==========================================
    // OBTENER TODAS LAS FACTURAS
    // ==========================================
    @GetMapping
    public ResponseEntity<List<Factura>> getAll() {
        List<Factura> facturas = facturaDAO.findAll();
        return ResponseEntity.ok(facturas);
    }

    // ==========================================
    // OBTENER FACTURA POR ID
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<Factura> getById(@PathVariable Long id) {
        Factura factura = facturaDAO.findById(id);

        if (factura == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(factura);
    }

    // ==========================================
    // DESCARGAR FACTURA EN P1DF
    // ==========================================
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> descargarFacturaPDF(@PathVariable Long id) {
        try {
            Factura factura = facturaDAO.findById(id);

            if (factura == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfBytes = facturaPDFService.generarFacturaPDF(factura);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "factura_" + factura.getIdFactura() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==========================================
    // ACTUALIZAR ESTADO DE UNA FACTURA
    // ==========================================
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {

        if (!body.containsKey("estado")) {
            return ResponseEntity.badRequest().build();
        }

        String newStatus = body.get("estado");

        boolean updated = facturaDAO.updateStatus(id, newStatus);

        if (!updated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    // ==========================================
    // GENERAR FACTURA A PARTIR DE UNA ORDEN
    // ==========================================
    @PostMapping("/{id}/facturar")
    public ResponseEntity<Factura> facturarOrden(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        try {
            if (!body.containsKey("costoManoObra") || !body.containsKey("costoRepuestos")) {
                return ResponseEntity.badRequest().build();
            }

            BigDecimal costoManoObra = new BigDecimal(body.get("costoManoObra"));
            BigDecimal costoRepuestos = new BigDecimal(body.get("costoRepuestos"));

            BigDecimal subtotal = costoManoObra.add(costoRepuestos);
            BigDecimal impuestos = subtotal.multiply(new BigDecimal("0.19"));
            BigDecimal total = subtotal.add(impuestos);

            // Crear la nueva factura
            Factura nuevaFactura = new Factura();
            nuevaFactura.setIdOrdenTrabajo(id);
            nuevaFactura.setFechaEmision(LocalDateTime.now());
            nuevaFactura.setCostoManoObra(costoManoObra);
            nuevaFactura.setCostoRepuestos(costoRepuestos);
            nuevaFactura.setImpuestos(impuestos);
            nuevaFactura.setValorTotal(total);
            nuevaFactura.setEstadoPago("Pagada");

            Factura guardada = facturaDAO.save(nuevaFactura);

            if (guardada == null) {
                return ResponseEntity.badRequest().build();
            }

            // Actualizar estado de la orden a "Facturada"
            ordenTrabajoDAO.updateStatus(id, "Facturada");

            return ResponseEntity.ok(guardada);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean crearFacturaEnTabla(Long ordenId, Double subtotal, Double iva, Double total) {
        try {
            // Verificar si existe la tabla Factura
            String sql = "INSERT INTO Factura (idOrden, fechaFactura, subtotal, iva, total, estado) VALUES (?, NOW(), ?, ?, ?, 'PAGADA')";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, ordenId);
                pstmt.setDouble(2, subtotal);
                pstmt.setDouble(3, iva);
                pstmt.setDouble(4, total);

                return pstmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("️ No se pudo crear factura en tabla específica: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
// OBTENER FACTURAS PENDIENTES
// ==========================================
    @GetMapping("/pendientes")
    public ResponseEntity<List<Factura>> getFacturasPendientes() {
        List<Factura> facturas = facturaDAO.findByEstado("Pendiente");
        return ResponseEntity.ok(facturas);
    }

    // ==========================================
// OBTENER FACTURAS PAGADAS
// ==========================================
    @GetMapping("/pagadas")
    public ResponseEntity<List<Factura>> getFacturasPagadas() {
        List<Factura> facturas = facturaDAO.findByEstado("Pagada");
        return ResponseEntity.ok(facturas);
    }

    // ==========================================
// MARCAR FACTURA COMO PAGADA
// ==========================================
    @PutMapping("/{id}/pagar")
    public ResponseEntity<Map<String, Object>> marcarComoPagada(@PathVariable Long id) {
        try {
            boolean success = facturaDAO.updateStatus(id, "Pagada");

            if (success) {
                return ResponseEntity.ok()
                        .body(Map.of("success", true, "message", "Factura marcada como pagada"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Factura no encontrada"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error interno del servidor"));
        }
    }
}