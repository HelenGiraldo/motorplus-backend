package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Repuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepuestoDAO {

    public List<Repuesto> findAll() {
        List<Repuesto> repuestos = new ArrayList<>();
        // ELIMINAR idProveedor del SELECT
        String sql = "SELECT idRepuesto, nombre, descripcion, costoUnitario, precioVenta, stockDisponible, stockMinimo FROM Repuesto";

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
        // ELIMINAR idProveedor del SELECT
        String sql = "SELECT idRepuesto, nombre, descripcion, costoUnitario, precioVenta, stockDisponible, stockMinimo FROM Repuesto WHERE idRepuesto = ?";

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
        // ELIMINAR idProveedor del INSERT
        String sql = "INSERT INTO Repuesto(nombre, descripcion, costoUnitario, precioVenta, stockDisponible, stockMinimo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // ELIMINAR la línea de idProveedor
            pstmt.setString(1, repuesto.getNombre());
            pstmt.setString(2, repuesto.getDescripcion());
            pstmt.setBigDecimal(3, repuesto.getCostoUnitario());
            pstmt.setBigDecimal(4, repuesto.getPrecioVenta());
            pstmt.setInt(5, repuesto.getStockDisponible());
            pstmt.setInt(6, repuesto.getStockMinimo());

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
        // ELIMINAR idProveedor del UPDATE
        String sql = "UPDATE Repuesto SET nombre = ?, descripcion = ?, costoUnitario = ?, precioVenta = ?, stockDisponible = ?, stockMinimo = ? WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, repuesto.getNombre());
            pstmt.setString(2, repuesto.getDescripcion());
            pstmt.setBigDecimal(3, repuesto.getCostoUnitario());
            pstmt.setBigDecimal(4, repuesto.getPrecioVenta());
            pstmt.setInt(5, repuesto.getStockDisponible());
            pstmt.setInt(6, repuesto.getStockMinimo());
            pstmt.setLong(7, id);

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

    // MÉTODO DELETE QUE FALTABA
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Repuesto WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarStock(Long idRepuesto, int cantidad) {
        String sql = "UPDATE Repuesto SET stockDisponible = stockDisponible + ? WHERE idRepuesto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidad); // Si 'cantidad' es -1 para restar, entonces stockDisponible + (-1) = stockDisponible - 1
            pstmt.setLong(2, idRepuesto);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR idProveedor del mapeo
    private Repuesto mapRowToRepuesto(ResultSet rs) throws SQLException {
        return new Repuesto(
                rs.getLong("idRepuesto"),
                // ELIMINAR: rs.getLong("idProveedor"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("costoUnitario"),
                rs.getBigDecimal("precioVenta"),
                rs.getInt("stockDisponible"),
                rs.getInt("stockMinimo")
        );
    }
}