package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdenRepuestoDAO {

    public List<Map<String, Object>> findByOrdenId(Long ordenId) {
        List<Map<String, Object>> repuestos = new ArrayList<>();
        // MEJORADO: Incluir más datos para la UI
        String sql = "SELECT r.idRepuesto, r.nombre, r.descripcion, orp.cantidad, orp.costoAlMomento, " +
                "(orp.cantidad * orp.costoAlMomento) as subtotal " +
                "FROM OrdenRepuesto orp " +
                "INNER JOIN Repuesto r ON orp.idRepuesto = r.idRepuesto " +
                "WHERE orp.idOrdenTrabajo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> repuesto = new HashMap<>();
                    repuesto.put("idRepuesto", rs.getLong("idRepuesto"));
                    repuesto.put("nombre", rs.getString("nombre"));
                    repuesto.put("descripcion", rs.getString("descripcion"));
                    repuesto.put("cantidad", rs.getInt("cantidad"));
                    repuesto.put("costoAlMomento", rs.getBigDecimal("costoAlMomento"));
                    repuesto.put("subtotal", rs.getBigDecimal("subtotal"));
                    repuestos.add(repuesto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repuestos;
    }

    public boolean save(Long ordenId, Long repuestoId, Integer cantidad, java.math.BigDecimal costoUnitario) {
        String sql = "INSERT INTO OrdenRepuesto (idOrdenTrabajo, idRepuesto, cantidad, costoAlMomento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, repuestoId);
            pstmt.setInt(3, cantidad);
            pstmt.setBigDecimal(4, costoUnitario);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // NUEVO: Método para eliminar repuesto de orden
    public boolean eliminarRepuestoDeOrden(Long ordenId, Long repuestoId) {
        String sql = "DELETE FROM OrdenRepuesto WHERE idOrdenTrabajo = ? AND idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, repuestoId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}