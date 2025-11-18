package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.CorreoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CorreoClienteDAO {

    public List<CorreoCliente> findByClienteId(Integer clienteId) {
        List<CorreoCliente> correos = new ArrayList<>();
        String sql = "SELECT * FROM CorreoCliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
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

    public CorreoCliente save(CorreoCliente correo) {
        String sql = "INSERT INTO CorreoCliente (email, tipo, esPrincipal, idCliente) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, correo.getEmail());
            pstmt.setString(2, correo.getTipo());
            pstmt.setBoolean(3, correo.getEsPrincipal());
            pstmt.setInt(4, correo.getIdCliente());

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
        String sql = "DELETE FROM CorreoCliente WHERE idCorreo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCorreo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private CorreoCliente mapRowToCorreo(ResultSet rs) throws SQLException {
        return new CorreoCliente(
                rs.getInt("idCorreo"),
                rs.getString("email"),
                rs.getString("tipo"),
                rs.getBoolean("esPrincipal"),
                rs.getInt("idCliente")
        );
    }
}