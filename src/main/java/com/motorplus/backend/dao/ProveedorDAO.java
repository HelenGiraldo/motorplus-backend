package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    private Connection conn = DatabaseConnection.getConnection();

    public List<Proveedor> findAll() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                proveedores.add(mapRowToProveedor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    public Proveedor findById(Long id) {
        String sql = "SELECT * FROM Proveedor WHERE idProveedor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProveedor(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Proveedor save(Proveedor proveedor) {
        String sql = "INSERT INTO Proveedor (nombre, nit, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getNit());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getDireccion());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    proveedor.setIdProveedor(generatedKeys.getLong(1));
                    return proveedor;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Proveedor update(Long id, Proveedor proveedor) {
        String sql = "UPDATE Proveedor SET nombre = ?, nit = ?, telefono = ?, direccion = ? WHERE idProveedor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getNit());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getDireccion());
            pstmt.setLong(5, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                proveedor.setIdProveedor(id);
                return proveedor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Proveedor WHERE idProveedor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Proveedor mapRowToProveedor(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getLong("idProveedor"),
                rs.getString("nombre"),
                rs.getString("nit"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );
    }
}