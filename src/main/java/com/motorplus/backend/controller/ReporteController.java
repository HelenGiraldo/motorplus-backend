package com.motorplus.backend.controller;

import com.motorplus.backend.service.ReporteService;
import com.motorplus.backend.service.ReportePdfService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService pdfService;

    public ReporteController(ReporteService reporteService, ReportePdfService pdfService) {
        this.reporteService = reporteService;
        this.pdfService = pdfService;
    }

    // ================================
    //          ENDPOINTS JSON (REALES)
    // ================================

    @GetMapping("/clientes")
    public List<Map<String, Object>> clientes() {
        return reporteService.reporteClientes();
    }

    @GetMapping("/mecanicos")
    public List<Map<String, Object>> mecanicos() {
        return reporteService.reporteMecanicos();
    }

    @GetMapping("/inventario")
    public List<Map<String, Object>> inventario() {
        return reporteService.reporteInventario();
    }

    @GetMapping("/facturas-pendientes")
    public List<Map<String, Object>> facturasPendientes() {
        return reporteService.reporteFacturasPendientes();
    }

    @GetMapping("/repuestos-usados")
    public List<Map<String, Object>> repuestosUsados() {
        return reporteService.reporteRepuestosUsados();
    }

    @GetMapping("/ordenes-mes/{anio}/{mes}")
    public List<Map<String, Object>> ordenesMes(@PathVariable int anio, @PathVariable int mes) {
        return reporteService.reporteOrdenesMes(anio, mes);
    }

    @GetMapping("/historial-vehiculo/{placa}")
    public List<Map<String, Object>> historialVehiculo(@PathVariable String placa) {
        return reporteService.reporteHistorialVehiculo(placa);
    }

    // **ENDPOINT ORIGINAL PARA GRAFICO - MANTENER DATOS REALES**
    @GetMapping("/ventas-mes")
    public List<Map<String, Object>> ventasMes() {
        // Siempre retornar datos reales del servicio
        return reporteService.reporteVentasMes();
    }

    @GetMapping("/mecanicos-productivos")
    public List<Map<String, Object>> mecanicosProductivos() {
        return reporteService.reporteMecanicosProductivos();
    }

    @GetMapping("/servicios-populares")
    public List<Map<String, Object>> serviciosPopulares() {
        return reporteService.reporteServiciosPopulares();
    }

    // ================================
    //          PDF (REALES)
    // ================================

    @GetMapping("/pdf/{tipo}")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable String tipo) {

        List<Map<String, Object>> datos;

        switch (tipo) {
            case "clientes" -> datos = reporteService.reporteClientes();
            case "mecanicos" -> datos = reporteService.reporteMecanicos();
            case "inventario" -> datos = reporteService.reporteInventario();
            case "facturas-pendientes" -> datos = reporteService.reporteFacturasPendientes();
            case "ventas-mes" -> datos = reporteService.reporteVentasMes(); // PDF de ventas reales
            default -> datos = List.of();
        }

        byte[] pdf = pdfService.generarPDF("Reporte: " + tipo.toUpperCase(), datos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + tipo + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}