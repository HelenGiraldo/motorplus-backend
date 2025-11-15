package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.OrdenTrabajo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoDAO {

    private Connection conn = DatabaseConnection.getConnection();

    public List<OrdenTrabajo> findAll() {
        List<OrdenTrabajo> ordenes = new ArrayList<>();
        String sql = "SELECT * FROM OrdenTrabajo";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ordenes.add(mapRowToOrdenTrabajo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordenes;
    }

    public OrdenTrabajo findById(Long id) {
        String sql = "SELECT * FROM OrdenTrabajo WHERE idOrdenTrabajo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOrdenTrabajo(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OrdenTrabajo save(OrdenTrabajo orden) {
        String sql = "INSERT INTO OrdenTrabajo (fechaIngreso, diagnosticoInicial, estado, idVehiculo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(orden.getFechaIngreso()));
            pstmt.setString(2, orden.getDiagnosticoInicial());
            pstmt.setString(3, orden.getEstado());
            pstmt.setLong(4, orden.getIdVehiculo());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orden.setIdOrdenTrabajo(generatedKeys.getLong(1));
                    return orden;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OrdenTrabajo mapRowToOrdenTrabajo(ResultSet rs) throws SQLException {
        return new OrdenTrabajo(
                rs.getLong("idOrdenTrabajo"),
                rs.getTimestamp("fechaIngreso").toLocalDateTime(),
                rs.getString("diagnosticoInicial"),
                rs.getString("estado"),
                rs.getLong("idVehiculo")
        );
    }

    // Faltarían update() y delete(), que son complejos (cancelaciones, etc.)
}