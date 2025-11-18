package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.CorreoProveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CorreoProveedorDAO {

    public List<CorreoProveedor> findByProveedorId(Integer proveedorId) {
        List<CorreoProveedor> correos = new ArrayList<>();
        String sql = "SELECT * FROM CorreoProveedor WHERE idProveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proveedorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    correos.add(mapRowToCorreo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return correos;
    }

    public CorreoProveedor save(CorreoProveedor correo) {
        String sql = "INSERT INTO CorreoProveedor (email, tipo, esPrincipal, idProveedor) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, correo.getEmail());
            pstmt.setString(2, correo.getTipo());
            pstmt.setBoolean(3, correo.getEsPrincipal());
            pstmt.setInt(4, correo.getIdProveedor());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    correo.setIdCorreo(generatedKeys.getInt(1));
                    return correo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer idCorreo) {
        String sql = "DELETE FROM CorreoProveedor WHERE idCorreo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCorreo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CorreoProveedor mapRowToCorreo(ResultSet rs) throws SQLException {
        return new CorreoProveedor(
                rs.getInt("idCorreo"),
                rs.getString("email"),
                rs.getString("tipo"),
                rs.getBoolean("esPrincipal"),
                rs.getInt("idProveedor")
        );
    }
}