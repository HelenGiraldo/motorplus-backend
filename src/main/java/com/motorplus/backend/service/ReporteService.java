package com.motorplus.backend.service;

import com.motorplus.backend.dao.ReporteDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final ReporteDAO reporteDAO;

    public ReporteService() {
        this.reporteDAO = new ReporteDAO();
    }

    // ====================== SIMPLES ======================
    public List<Map<String, Object>> getClientes() {
        return reporteDAO.getReporteClientes();
    }

    public List<Map<String, Object>> getMecanicos() {
        return reporteDAO.getReporteMecanicos();
    }

    public List<Map<String, Object>> getInventario() {
        return reporteDAO.getReporteInventario();
    }

    public List<Map<String, Object>> getFacturasPendientes() {
        return reporteDAO.getReporteFacturasPendientes();
    }

    public List<Map<String, Object>> getRepuestosUsados() {
        return reporteDAO.getReporteRepuestosUsados();
    }

    // ====================== INTERMEDIOS ======================
    public List<Map<String, Object>> getOrdenesMes(int anio, int mes) {
        return reporteDAO.getReporteOrdenesMes(anio, mes);
    }

    // NUEVO MÉTODO PARA DATOS DE GRÁFICA
    public List<Map<String, Object>> getOrdenesMesParaGrafica(int anio, int mes) {
        return reporteDAO.getReporteOrdenesMesParaGrafica(anio, mes);
    }

    public List<Map<String, Object>> getHistorialVehiculo(String placa) {
        return reporteDAO.getReporteHistorialVehiculo(placa);
    }

    // ====================== VEHÍCULOS ======================
    public List<Map<String, Object>> getVehiculosTodos() {
        System.out.println(" Service: getVehiculosTodos");
        return reporteDAO.getReporteTodosVehiculos();
    }

    public List<Map<String, Object>> getVehiculosActivos() {
        System.out.println(" Service: getVehiculosActivos");
        return reporteDAO.getReporteVehiculosActivos();
    }

    public List<Map<String, Object>> getVehiculosInactivos() {
        System.out.println(" Service: getVehiculosInactivos");
        return reporteDAO.getReporteVehiculosInactivos();
    }

    // ====================== GRAFICOS ======================
    public List<Map<String, Object>> getVentasMes() {
        return reporteDAO.getReporteVentasMes();
    }

    public List<Map<String, Object>> getMecanicosProductivos() {
        return reporteDAO.getReporteMecanicosProductivos();
    }

    public List<Map<String, Object>> getServiciosPopulares() {
        return reporteDAO.getReporteServiciosPopulares();
    }

    public List<Map<String, Object>> getEficienciaTaller() {
        return reporteDAO.getReporteEficienciaTaller();
    }

    // ====================== ADICIONALES ======================
    public List<Map<String, Object>> getIngresosMensuales() {
        System.out.println("Service: getIngresosMensuales");
        return reporteDAO.getReporteIngresosMensuales();
    }
}