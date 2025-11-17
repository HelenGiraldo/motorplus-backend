package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Repuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAO {

    public List<Repuesto> findAll() {
        List<Repuesto> repuestos = new ArrayList<>();
        String sql = "SELECT * FROM Repuesto";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                repuestos.add(mapRowToRepuesto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repuestos;
    }

    public Repuesto findById(Long id) {
        String sql = "SELECT * FROM Repuesto WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRepuesto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Repuesto save(Repuesto repuesto) {
        String sql = "INSERT INTO Repuesto (idProveedor, nombre, descripcion, costoUnitario, precioVenta, stockDisponible, stockMinimo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, repuesto.getIdProveedor());
            pstmt.setString(2, repuesto.getNombre());
            pstmt.setString(3, repuesto.getDescripcion());
            pstmt.setBigDecimal(4, repuesto.getCostoUnitario());
            pstmt.setBigDecimal(5, repuesto.getPrecioVenta());
            pstmt.setInt(6, repuesto.getStockDisponible());
            pstmt.setInt(7, repuesto.getStockMinimo());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    repuesto.setIdRepuesto(generatedKeys.getLong(1));
                    return repuesto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Repuesto update(Long id, Repuesto repuesto) {
        String sql = "UPDATE Repuesto SET idProveedor = ?, nombre = ?, descripcion = ?, costoUnitario = ?, precioVenta = ?, stockDisponible = ?, stockMinimo = ? WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, repuesto.getIdProveedor());
            pstmt.setString(2, repuesto.getNombre());
            pstmt.setString(3, repuesto.getDescripcion());
            pstmt.setBigDecimal(4, repuesto.getCostoUnitario());
            pstmt.setBigDecimal(5, repuesto.getPrecioVenta());
            pstmt.setInt(6, repuesto.getStockDisponible());
            pstmt.setInt(7, repuesto.getStockMinimo());
            pstmt.setLong(8, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                repuesto.setIdRepuesto(id);
                return repuesto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Repuesto WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Repuesto mapRowToRepuesto(ResultSet rs) throws SQLException {
        return new Repuesto(
                rs.getLong("idRepuesto"),
                rs.getLong("idProveedor"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("costoUnitario"),
                rs.getBigDecimal("precioVenta"),
                rs.getInt("stockDisponible"),
                rs.getInt("stockMinimo")
        );
    }
}
