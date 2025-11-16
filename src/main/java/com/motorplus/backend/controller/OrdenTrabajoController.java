package com.motorplus.backend.controller;

import com.motorplus.backend.dao.FacturaDAO;
import com.motorplus.backend.dao.OrdenTrabajoDAO;
import com.motorplus.backend.entity.Factura;
import com.motorplus.backend.entity.OrdenTrabajo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes-trabajo")
@CrossOrigin(origins = "*")
public class OrdenTrabajoController {

    private OrdenTrabajoDAO ordenTrabajoDAO = new OrdenTrabajoDAO();
    private FacturaDAO facturaDAO = new FacturaDAO();

    @GetMapping
    public List<OrdenTrabajo> getAll() {
        return ordenTrabajoDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajo> getById(@PathVariable Long id) {
        OrdenTrabajo orden = ordenTrabajoDAO.findById(id);
        return (orden != null) ? ResponseEntity.ok(orden) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public OrdenTrabajo create(@RequestBody OrdenTrabajo ordenTrabajo) {
        return ordenTrabajoDAO.save(ordenTrabajo);
    }

    @PostMapping("/{id}/facturar")
    public ResponseEntity<Factura> facturarOrden(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            BigDecimal costoManoObra = new BigDecimal(body.get("costoManoObra"));
            BigDecimal costoRepuestos = new BigDecimal(body.get("costoRepuestos"));

            // Calcular impuestos (19% en Colombia)
            BigDecimal subtotal = costoManoObra.add(costoRepuestos);
            BigDecimal impuestos = subtotal.multiply(new BigDecimal("0.19"));
            BigDecimal total = subtotal.add(impuestos);

            // 1. Crear la Factura
            Factura nuevaFactura = new Factura();
            nuevaFactura.setIdOrdenTrabajo(id);
            nuevaFactura.setCostoManoObra(costoManoObra);
            nuevaFactura.setCostoRepuestos(costoRepuestos);
            nuevaFactura.setImpuestos(impuestos);
            nuevaFactura.setValorTotal(total);
            nuevaFactura.setEstadoPago("Pagada"); // O "Pendiente" si prefieres

            Factura facturaGuardada = facturaDAO.save(nuevaFactura);

            if (facturaGuardada == null) {
                throw new Exception("No se pudo guardar la factura");
            }

            // 2. Actualizar estado de la Orden
            ordenTrabajoDAO.updateStatus(id, "Facturada");

            return ResponseEntity.ok(facturaGuardada);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}