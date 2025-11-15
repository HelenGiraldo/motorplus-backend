package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteDAO {

    private Connection conn = DatabaseConnection.getConnection();

    private List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(md.getColumnLabel(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Reporte 1
    public List<Map<String, Object>> getReporteClientes() {
        String sql = "SELECT documento, nombres, apellidos, telefono, email FROM Cliente ORDER BY apellidos";
        return executeQuery(sql);
    }

    // Reporte 2
    public List<Map<String, Object>> getReporteMecanicos() {
        String sql = "SELECT documento, nombres, apellidos, especialidad FROM Mecanico ORDER BY especialidad, apellidos";
        return executeQuery(sql);
    }

    // Reporte 3
    public List<Map<String, Object>> getReporteInventario() {
        String sql = "SELECT r.nombre, r.stockDisponible, r.costoUnitario, p.nombre AS proveedor " +
                "FROM Repuesto r " +
                "JOIN Proveedor p ON r.idProveedor = p.idProveedor " +
                "ORDER BY r.stockDisponible ASC";
        return executeQuery(sql);
    }

    // Reporte 4
    public List<Map<String, Object>> getReporteOrdenesMes(int anio, int mes) {
        String sql = "SELECT ot.idOrdenTrabajo, ot.fechaIngreso, ot.estado, v.placa, c.nombres, c.apellidos " +
                "FROM OrdenTrabajo ot " +
                "JOIN Vehiculo v ON ot.idVehiculo = v.idVehiculo " +
                "JOIN Cliente c ON v.idCliente = c.idCliente " +
                "WHERE YEAR(ot.fechaIngreso) = ? AND MONTH(ot.fechaIngreso) = ? " +
                "ORDER BY ot.fechaIngreso DESC";
        return executeQuery(sql, anio, mes);
    }

    // Reporte 5
    public List<Map<String, Object>> getReporteFacturasPendientes() {
        String sql = "SELECT f.idFactura, f.fechaEmision, f.valorTotal, c.nombres, c.apellidos " +
                "FROM Factura f " +
                "JOIN OrdenTrabajo ot ON f.idOrdenTrabajo = ot.idOrdenTrabajo " +
                "JOIN Vehiculo v ON ot.idVehiculo = v.idVehiculo " +
                "JOIN Cliente c ON v.idCliente = c.idCliente " +
                "WHERE f.estadoPago = 'Pendiente' " +
                "ORDER BY f.fechaEmision ASC";
        return executeQuery(sql);
    }

    // Reporte 6
    public List<Map<String, Object>> getReporteRepuestosUsados() {
        String sql = "SELECT r.nombre, SUM(or_rep.cantidad) AS totalUsado " +
                "FROM OrdenRepuesto or_rep " +
                "JOIN Repuesto r ON or_rep.idRepuesto = r.idRepuesto " +
                "GROUP BY r.nombre " +
                "ORDER BY totalUsado DESC " +
                "LIMIT 10";
        return executeQuery(sql);
    }

    // Reporte 7
    public List<Map<String, Object>> getReporteHistorialVehiculo(String placa) {
        String sql = "SELECT ot.fechaIngreso, ot.diagnosticoInicial, f.valorTotal, ot.estado " +
                "FROM OrdenTrabajo ot " +
                "JOIN Vehiculo v ON ot.idVehiculo = v.idVehiculo " +
                "LEFT JOIN Factura f ON ot.idOrdenTrabajo = f.idOrdenTrabajo " +
                "WHERE v.placa = ? " +
                "ORDER BY ot.fechaIngreso DESC";
        return executeQuery(sql, placa);
    }

    // Reporte 8
    public List<Map<String, Object>> getReporteVentasMes() {
        String sql = "SELECT DATE_FORMAT(fechaEmision, '%Y-%m') AS mes, SUM(valorTotal) AS totalVentas " +
                "FROM Factura " +
                "WHERE estadoPago = 'Pagada' " +
                "GROUP BY mes " +
                "ORDER BY mes ASC";
        return executeQuery(sql);
    }

    // Reporte 9
    public List<Map<String, Object>> getReporteMecanicosProductivos() {
        String sql = "SELECT m.nombres, m.apellidos, COUNT(om.idOrdenTrabajo) AS totalOrdenes " +
                "FROM OrdenMecanico om " +
                "JOIN Mecanico m ON om.idMecanico = m.idMecanico " +
                "GROUP BY m.idMecanico, m.nombres, m.apellidos " +
                "ORDER BY totalOrdenes DESC " +
                "LIMIT 5";
        return executeQuery(sql);
    }

    // Reporte 10
    public List<Map<String, Object>> getReporteServiciosPopulares() {
        String sql = "SELECT s.nombre, COUNT(os.idServicio) AS cantidad " +
                "FROM OrdenServicio os " +
                "JOIN Servicio s ON os.idServicio = s.idServicio " +
                "GROUP BY s.nombre " +
                "ORDER BY cantidad DESC";
        return executeQuery(sql);
    }
}