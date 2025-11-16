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

    public OrdenTrabajo update(Long id, OrdenTrabajo orden) {
        // Generalmente solo se actualiza el diagnóstico o el estado
        String sql = "UPDATE OrdenTrabajo SET diagnosticoInicial = ?, estado = ? WHERE idOrdenTrabajo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orden.getDiagnosticoInicial());
            pstmt.setString(2, orden.getEstado());
            pstmt.setLong(3, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                orden.setIdOrdenTrabajo(id);
                return orden;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        // ADVERTENCIA: Esto fallará si la orden tiene facturas, repuestos, etc., asociados (Foreign Keys).
        // En un sistema real, se usaría un borrado en cascada o un "borrado lógico" (estado='Cancelada')
        String sql = "DELETE FROM OrdenTrabajo WHERE idOrdenTrabajo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Imprime el error de FK si ocurre
            e.printStackTrace();
        }
        return false;
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

    public boolean updateStatus(Long id, String newStatus) {
        String sql = "UPDATE OrdenTrabajo SET estado = ? WHERE idOrdenTrabajo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setLong(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}