package com.motorplus.backend.controller;

import com.motorplus.backend.dao.FacturaDAO;
import com.motorplus.backend.dao.OrdenTrabajoDAO;
import com.motorplus.backend.entity.Factura;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final OrdenTrabajoDAO ordenTrabajoDAO = new OrdenTrabajoDAO();

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
}
