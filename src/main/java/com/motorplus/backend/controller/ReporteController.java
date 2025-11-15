package com.motorplus.backend.controller;

import com.motorplus.backend.dao.ReporteDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private ReporteDAO reporteDAO = new ReporteDAO();

    @GetMapping("/simple/clientes")
    public ResponseEntity<List<Map<String, Object>>> getReporteClientes() {
        return ResponseEntity.ok(reporteDAO.getReporteClientes());
    }

    @GetMapping("/simple/mecanicos")
    public ResponseEntity<List<Map<String, Object>>> getReporteMecanicos() {
        return ResponseEntity.ok(reporteDAO.getReporteMecanicos());
    }

    @GetMapping("/simple/inventario")
    public ResponseEntity<List<Map<String, Object>>> getReporteInventario() {
        return ResponseEntity.ok(reporteDAO.getReporteInventario());
    }

    @GetMapping("/intermedio/ordenes-mes")
    public ResponseEntity<List<Map<String, Object>>> getReporteOrdenesMes() {
        LocalDate now = LocalDate.now();
        return ResponseEntity.ok(reporteDAO.getReporteOrdenesMes(now.getYear(), now.getMonthValue()));
    }

    @GetMapping("/intermedio/facturas-pendientes")
    public ResponseEntity<List<Map<String, Object>>> getReporteFacturasPendientes() {
        return ResponseEntity.ok(reporteDAO.getReporteFacturasPendientes());
    }

    @GetMapping("/intermedio/repuestos-usados")
    public ResponseEntity<List<Map<String, Object>>> getReporteRepuestosUsados() {
        return ResponseEntity.ok(reporteDAO.getReporteRepuestosUsados());
    }

    @GetMapping("/intermedio/historial-vehiculo/{placa}")
    public ResponseEntity<List<Map<String, Object>>> getReporteHistorialVehiculo(@PathVariable String placa) {
        return ResponseEntity.ok(reporteDAO.getReporteHistorialVehiculo(placa));
    }

    @GetMapping("/complejo/ventas-mes")
    public ResponseEntity<List<Map<String, Object>>> getReporteVentasMes() {
        return ResponseEntity.ok(reporteDAO.getReporteVentasMes());
    }

    @GetMapping("/complejo/mecanicos-productivos")
    public ResponseEntity<List<Map<String, Object>>> getReporteMecanicosProductivos() {
        return ResponseEntity.ok(reporteDAO.getReporteMecanicosProductivos());
    }

    @GetMapping("/complejo/servicios-populares")
    public ResponseEntity<List<Map<String, Object>>> getReporteServiciosPopulares() {
        return ResponseEntity.ok(reporteDAO.getReporteServiciosPopulares());
    }
}