package com.motorplus.backend.controller;

import com.motorplus.backend.service.ReporteService;
import com.motorplus.backend.service.ReportePdfService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
    //          ENDPOINTS EXISTENTES
    // ================================

    @GetMapping("/clientes")
    public List<Map<String, Object>> clientes() {
        return reporteService.getClientes(); // CAMBIADO: getClientes()
    }

    @GetMapping("/mecanicos")
    public List<Map<String, Object>> mecanicos() {
        return reporteService.getMecanicos(); // CAMBIADO: getMecanicos()
    }

    @GetMapping("/inventario")
    public List<Map<String, Object>> inventario() {
        return reporteService.getInventario(); // CAMBIADO: getInventario()
    }

    @GetMapping("/facturas-pendientes")
    public List<Map<String, Object>> facturasPendientes() {
        return reporteService.getFacturasPendientes(); // CAMBIADO: getFacturasPendientes()
    }

    @GetMapping("/repuestos-usados")
    public List<Map<String, Object>> repuestosUsados() {
        return reporteService.getRepuestosUsados(); // CAMBIADO: getRepuestosUsados()
    }

    @GetMapping("/ordenes-mes/{anio}/{mes}")
    public List<Map<String, Object>> ordenesMes(@PathVariable int anio, @PathVariable int mes) {
        return reporteService.getOrdenesMes(anio, mes); // CAMBIADO: getOrdenesMes()
    }

    // NUEVO ENDPOINT PARA GRÁFICA
    @GetMapping("/ordenes-mes-grafica/{anio}/{mes}")
    public List<Map<String, Object>> ordenesMesParaGrafica(@PathVariable int anio, @PathVariable int mes) {
        return reporteService.getOrdenesMesParaGrafica(anio, mes);
    }

    @GetMapping("/historial-vehiculo/{placa}")
    public List<Map<String, Object>> historialVehiculo(@PathVariable String placa) {
        return reporteService.getHistorialVehiculo(placa); // CAMBIADO: getHistorialVehiculo()
    }

    @GetMapping("/ventas-mes")
    public List<Map<String, Object>> ventasMes() {
        return reporteService.getVentasMes(); // CAMBIADO: getVentasMes()
    }

    @GetMapping("/mecanicos-productivos")
    public List<Map<String, Object>> mecanicosProductivos() {
        return reporteService.getMecanicosProductivos(); // CAMBIADO: getMecanicosProductivos()
    }

    @GetMapping("/servicios-populares")
    public List<Map<String, Object>> serviciosPopulares() {
        return reporteService.getServiciosPopulares(); // CAMBIADO: getServiciosPopulares()
    }

    @GetMapping("/eficiencia-taller")
    public List<Map<String, Object>> eficienciaTaller() {
        return reporteService.getEficienciaTaller(); // CAMBIADO: getEficienciaTaller()
    }

    // ================================
    //    NUEVOS ENDPOINTS VEHÍCULOS
    // ================================

    @GetMapping("/vehiculos-todos")
    public List<Map<String, Object>> vehiculosTodos() {
        System.out.println(" Ejecutando endpoint: /vehiculos-todos");
        return reporteService.getVehiculosTodos(); // CAMBIADO: getReporteVehiculosTodos()
    }

    @GetMapping("/vehiculos-activos")
    public List<Map<String, Object>> vehiculosActivos() {
        System.out.println(" Ejecutando endpoint: /vehiculos-activos");
        return reporteService.getVehiculosActivos(); // CAMBIADO: getReporteVehiculosActivos()
    }

    @GetMapping("/vehiculos-inactivos")
    public List<Map<String, Object>> vehiculosInactivos() {
        System.out.println(" Ejecutando endpoint: /vehiculos-inactivos");
        return reporteService.getVehiculosInactivos(); // CAMBIADO: getReporteVehiculosInactivos()
    }

    @GetMapping("/ingresos-mensuales")
    public List<Map<String, Object>> ingresosMensuales() {
        System.out.println(" Ejecutando endpoint: /ingresos-mensuales");
        return reporteService.getIngresosMensuales(); // CAMBIADO: getReporteIngresosMensuales()
    }

    // ================================
    //          PDF
    // ================================

    @GetMapping("/pdf/{tipo}")
    public ResponseEntity<byte[]> exportarPDF(@PathVariable String tipo) {

        List<Map<String, Object>> datos;

        switch (tipo) {
            case "clientes" -> datos = reporteService.getClientes();
            case "mecanicos" -> datos = reporteService.getMecanicos();
            case "inventario" -> datos = reporteService.getInventario();
            case "facturas-pendientes" -> datos = reporteService.getFacturasPendientes();
            case "ventas-mes" -> datos = reporteService.getVentasMes();
            case "vehiculos-todos" -> datos = reporteService.getVehiculosTodos();
            case "vehiculos-activos" -> datos = reporteService.getVehiculosActivos();
            case "vehiculos-inactivos" -> datos = reporteService.getVehiculosInactivos();
            case "ingresos-mensuales" -> datos = reporteService.getIngresosMensuales();
            default -> datos = List.of();
        }

        byte[] pdf = pdfService.generarPDF("Reporte: " + tipo.toUpperCase(), datos);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + tipo + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}