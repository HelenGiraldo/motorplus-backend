package com.motorplus.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    // TODO: La Persona 3 debe inyectar un 'ReporteService' aquí
    // y crear 10 endpoints que llamen a consultas SQL.

    @GetMapping("/simple/clientes")
    public ResponseEntity<Object> getReporteClientes() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Listado de Clientes..."));
    }

    @GetMapping("/simple/mecanicos")
    public ResponseEntity<Object> getReporteMecanicos() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Listado de Mecánicos..."));
    }

    @GetMapping("/simple/inventario")
    public ResponseEntity<Object> getReporteInventario() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Inventario de Repuestos..."));
    }

    @GetMapping("/intermedio/ordenes-mes")
    public ResponseEntity<Object> getReporteOrdenesMes() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Órdenes del mes..."));
    }

    @GetMapping("/intermedio/facturas-pendientes")
    public ResponseEntity<Object> getReporteFacturasPendientes() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Facturas pendientes de pago..."));
    }

    @GetMapping("/intermedio/repuestos-usados")
    public ResponseEntity<Object> getReporteRepuestosUsados() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Repuestos más usados..."));
    }

    @GetMapping("/intermedio/historial-vehiculo")
    public ResponseEntity<Object> getReporteHistorialVehiculo() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Historial de un vehículo..."));
    }

    @GetMapping("/complejo/ventas-mes")
    public ResponseEntity<Object> getReporteVentasMes() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Gráfico Ventas por Mes..."));
    }

    @GetMapping("/complejo/mecanicos-productivos")
    public ResponseEntity<Object> getReporteMecanicosProductivos() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Gráfico Top 5 Mecánicos..."));
    }

    @GetMapping("/complejo/servicios-populares")
    public ResponseEntity<Object> getReporteServiciosPopulares() {
        return ResponseEntity.ok(Map.of("reporte", "Pendiente: Gráfico Servicios más solicitados..."));
    }
}