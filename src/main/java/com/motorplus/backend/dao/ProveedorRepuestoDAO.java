package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.ProveedorRepuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorRepuestoDAO {

    public List<ProveedorRepuesto> findAll() {
        List<ProveedorRepuesto> proveedorRepuestos = new ArrayList<>();
        String sql = """
            SELECT pr.*, p.nombre as nombreProveedor, r.nombre as nombreRepuesto 
            FROM ProveedorRepuesto pr
            JOIN Proveedor p ON pr.idProveedor = p.idProveedor
            JOIN Repuesto r ON pr.idRepuesto = r.idRepuesto
            ORDER BY p.nombre, r.nombre
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                proveedorRepuestos.add(mapRowToProveedorRepuesto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedorRepuestos;
    }

    public ProveedorRepuesto findById(Long id) {
        String sql = """
            SELECT pr.*, p.nombre as nombreProveedor, r.nombre as nombreRepuesto 
            FROM ProveedorRepuesto pr
            JOIN Proveedor p ON pr.idProveedor = p.idProveedor
            JOIN Repuesto r ON pr.idRepuesto = r.idRepuesto
            WHERE pr.idProveedorRepuesto = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProveedorRepuesto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ProveedorRepuesto> findByProveedorId(Integer idProveedor) {
        List<ProveedorRepuesto> proveedorRepuestos = new ArrayList<>();
        String sql = """
            SELECT pr.*, p.nombre as nombreProveedor, r.nombre as nombreRepuesto 
            FROM ProveedorRepuesto pr
            JOIN Proveedor p ON pr.idProveedor = p.idProveedor
            JOIN Repuesto r ON pr.idRepuesto = r.idRepuesto
            WHERE pr.idProveedor = ?
            ORDER BY r.nombre
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProveedor);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    proveedorRepuestos.add(mapRowToProveedorRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedorRepuestos;
    }

    public List<ProveedorRepuesto> findByRepuestoId(Long idRepuesto) {
        List<ProveedorRepuesto> proveedorRepuestos = new ArrayList<>();
        String sql = """
            SELECT pr.*, p.nombre as nombreProveedor, r.nombre as nombreRepuesto 
            FROM ProveedorRepuesto pr
            JOIN Proveedor p ON pr.idProveedor = p.idProveedor
            JOIN Repuesto r ON pr.idRepuesto = r.idRepuesto
            WHERE pr.idRepuesto = ?
            ORDER BY p.nombre
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, idRepuesto);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    proveedorRepuestos.add(mapRowToProveedorRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedorRepuestos;
    }

    public ProveedorRepuesto save(ProveedorRepuesto proveedorRepuesto) {
        String sql = "INSERT INTO ProveedorRepuesto (idProveedor, idRepuesto, precioUnitario, stockDisponible) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, proveedorRepuesto.getIdProveedor());
            pstmt.setLong(2, proveedorRepuesto.getIdRepuesto());
            pstmt.setBigDecimal(3, proveedorRepuesto.getPrecioUnitario());
            pstmt.setInt(4, proveedorRepuesto.getStockDisponible());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        proveedorRepuesto.setIdProveedorRepuesto(generatedKeys.getLong(1));
                        return proveedorRepuesto;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ProveedorRepuesto update(Long id, ProveedorRepuesto proveedorRepuesto) {
        String sql = "UPDATE ProveedorRepuesto SET precioUnitario = ?, stockDisponible = ? WHERE idProveedorRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, proveedorRepuesto.getPrecioUnitario());
            pstmt.setInt(2, proveedorRepuesto.getStockDisponible());
            pstmt.setLong(3, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                proveedorRepuesto.setIdProveedorRepuesto(id);
                return proveedorRepuesto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM ProveedorRepuesto WHERE idProveedorRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByProveedorAndRepuesto(Integer idProveedor, Long idRepuesto) {
        String sql = "SELECT 1 FROM ProveedorRepuesto WHERE idProveedor = ? AND idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProveedor);
            pstmt.setLong(2, idRepuesto);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // EN ProveedorRepuestoDAO.java - AGREGAR ESTOS MÉTODOS

    public boolean restarStockRepuesto(Long idRepuesto, int cantidad) {
        String sql = "UPDATE ProveedorRepuesto SET stockDisponible = stockDisponible - ? WHERE idRepuesto = ? AND stockDisponible >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidad);
            pstmt.setLong(2, idRepuesto);
            pstmt.setInt(3, cantidad);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarStockDisponible(Long idRepuesto, int cantidadRequerida) {
        String sql = "SELECT stockDisponible FROM ProveedorRepuesto WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, idRepuesto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int stockActual = rs.getInt("stockDisponible");
                    return stockActual >= cantidadRequerida;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ProveedorRepuesto mapRowToProveedorRepuesto(ResultSet rs) throws SQLException {
        ProveedorRepuesto proveedorRepuesto = new ProveedorRepuesto();
        proveedorRepuesto.setIdProveedorRepuesto(rs.getLong("idProveedorRepuesto"));
        proveedorRepuesto.setIdProveedor(rs.getInt("idProveedor"));
        proveedorRepuesto.setIdRepuesto(rs.getLong("idRepuesto"));
        proveedorRepuesto.setPrecioUnitario(rs.getBigDecimal("precioUnitario"));
        proveedorRepuesto.setStockDisponible(rs.getInt("stockDisponible"));
        proveedorRepuesto.setNombreProveedor(rs.getString("nombreProveedor"));
        proveedorRepuesto.setNombreRepuesto(rs.getString("nombreRepuesto"));
        return proveedorRepuesto;
    }
}