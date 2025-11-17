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
    public List<Map<String, Object>> reporteClientes() { return reporteDAO.getReporteClientes(); }
    public List<Map<String, Object>> reporteMecanicos() { return reporteDAO.getReporteMecanicos(); }
    public List<Map<String, Object>> reporteInventario() { return reporteDAO.getReporteInventario(); }
    public List<Map<String, Object>> reporteFacturasPendientes() { return reporteDAO.getReporteFacturasPendientes(); }
    public List<Map<String, Object>> reporteRepuestosUsados() { return reporteDAO.getReporteRepuestosUsados(); }

    // ====================== INTERMEDIOS ======================
    public List<Map<String, Object>> reporteOrdenesMes(int anio, int mes) { return reporteDAO.getReporteOrdenesMes(anio, mes); }
    public List<Map<String, Object>> reporteHistorialVehiculo(String placa) { return reporteDAO.getReporteHistorialVehiculo(placa); }

    // ====================== GRAFICOS ======================
    public List<Map<String, Object>> reporteVentasMes() { return reporteDAO.getReporteVentasMes(); }
    public List<Map<String, Object>> reporteMecanicosProductivos() { return reporteDAO.getReporteMecanicosProductivos(); }
    public List<Map<String, Object>> reporteServiciosPopulares() { return reporteDAO.getReporteServiciosPopulares(); }
}

