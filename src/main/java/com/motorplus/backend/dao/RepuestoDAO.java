package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Repuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAO {

    private Connection conn = DatabaseConnection.getConnection();

    public List<Repuesto> findAll() {
        List<Repuesto> repuestos = new ArrayList<>();
        String sql = "SELECT * FROM Repuesto";
        try (Statement stmt = conn.createStatement();
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        String sql = "INSERT INTO Repuesto (nombre, descripcion, costoUnitario, stockDisponible, idProveedor) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, repuesto.getNombre());
            pstmt.setString(2, repuesto.getDescripcion());
            pstmt.setBigDecimal(3, repuesto.getCostoUnitario());
            pstmt.setInt(4, repuesto.getStockDisponible());
            pstmt.setLong(5, repuesto.getIdProveedor());

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
        String sql = "UPDATE Repuesto SET nombre = ?, descripcion = ?, costoUnitario = ?, stockDisponible = ?, idProveedor = ? WHERE idRepuesto = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, repuesto.getNombre());
            pstmt.setString(2, repuesto.getDescripcion());
            pstmt.setBigDecimal(3, repuesto.getCostoUnitario());
            pstmt.setInt(4, repuesto.getStockDisponible());
            pstmt.setLong(5, repuesto.getIdProveedor());
            pstmt.setLong(6, id);

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
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("costoUnitario"),
                rs.getInt("stockDisponible"),
                rs.getLong("idProveedor")
        );
    }
}