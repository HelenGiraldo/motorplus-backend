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

    // Accede a los reportes de complejidad simple/intermedia
    @GetMapping("/listado/{nombreReporte}")
    public ResponseEntity<List<Map<String, Object>>> getListadoReporte(@PathVariable String nombreReporte) {
        switch (nombreReporte) {
            case "clientes":
                return ResponseEntity.ok(reporteDAO.getReporteClientes());
            case "mecanicos":
                return ResponseEntity.ok(reporteDAO.getReporteMecanicos());
            case "inventario":
                return ResponseEntity.ok(reporteDAO.getReporteInventario());
            case "pendientes":
                return ResponseEntity.ok(reporteDAO.getReporteFacturasPendientes());
            case "usados":
                return ResponseEntity.ok(reporteDAO.getReporteRepuestosUsados());
            // Nota: Historial de vehículo y Órdenes por mes requieren parámetros adicionales.
            default:
                return ResponseEntity.notFound().build();
        }
    }

    // Accede a los reportes complejos (gráficos)
    @GetMapping("/graficos/{nombreGrafico}")
    public ResponseEntity<List<Map<String, Object>>> getDatosGrafico(@PathVariable String nombreGrafico) {
        switch (nombreGrafico) {
            case "ventas-mes":
                return ResponseEntity.ok(reporteDAO.getReporteVentasMes());
            case "productivos":
                return ResponseEntity.ok(reporteDAO.getReporteMecanicosProductivos());
            case "populares":
                return ResponseEntity.ok(reporteDAO.getReporteServiciosPopulares());
            default:
                return ResponseEntity.notFound().build();
        }
    }
}