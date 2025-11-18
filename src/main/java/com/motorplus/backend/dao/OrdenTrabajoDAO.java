package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.OrdenTrabajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoDAO {

    public List<OrdenTrabajo> findAll() {
        String sql = "SELECT * FROM OrdenTrabajo";
        List<OrdenTrabajo> ordenes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OrdenTrabajo orden = new OrdenTrabajo();
                orden.setIdOrdenTrabajo(rs.getLong("idOrdenTrabajo"));
                orden.setFechaIngreso(rs.getTimestamp("fechaIngreso").toLocalDateTime());
                orden.setDiagnosticoInicial(rs.getString("diagnosticoInicial"));
                orden.setEstado(rs.getString("estado"));
                orden.setIdVehiculo(rs.getLong("idVehiculo"));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordenes;
    }

    public OrdenTrabajo findById(Long id) {
        String sql = "SELECT * FROM OrdenTrabajo WHERE idOrdenTrabajo = ?";
        OrdenTrabajo orden = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    orden = new OrdenTrabajo();
                    orden.setIdOrdenTrabajo(rs.getLong("idOrdenTrabajo"));
                    orden.setFechaIngreso(rs.getTimestamp("fechaIngreso").toLocalDateTime());
                    orden.setDiagnosticoInicial(rs.getString("diagnosticoInicial"));
                    orden.setEstado(rs.getString("estado"));
                    orden.setIdVehiculo(rs.getLong("idVehiculo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orden;
    }

    public boolean updateStatus(Long id, String newStatus) {
        String sql = "UPDATE OrdenTrabajo SET estado = ? WHERE idOrdenTrabajo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setLong(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public OrdenTrabajo save(OrdenTrabajo ordenTrabajo) {
        String sql = "INSERT INTO OrdenTrabajo (fechaIngreso, diagnosticoInicial, estado, idVehiculo) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(ordenTrabajo.getFechaIngreso()));
            pstmt.setString(2, ordenTrabajo.getDiagnosticoInicial());
            pstmt.setString(3, ordenTrabajo.getEstado());
            pstmt.setLong(4, ordenTrabajo.getIdVehiculo());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ordenTrabajo.setIdOrdenTrabajo(generatedKeys.getLong(1));
                    return ordenTrabajo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean facturarOrden(Long ordenId, Double subtotal, Double iva, Double total, String estado) {
        // CORREGIDO: Usar el nombre correcto de la tabla y columnas
        String sql = "UPDATE OrdenTrabajo SET subtotal = ?, iva = ?, total = ?, estado = ? WHERE idOrdenTrabajo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, subtotal);
            pstmt.setDouble(2, iva);
            pstmt.setDouble(3, total);
            pstmt.setString(4, estado);
            pstmt.setLong(5, ordenId);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Filas afectadas al facturar: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error en facturarOrden: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}