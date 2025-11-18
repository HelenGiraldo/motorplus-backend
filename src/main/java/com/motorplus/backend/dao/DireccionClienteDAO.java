package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.DireccionCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DireccionClienteDAO {

    public List<DireccionCliente> findByClienteId(Integer clienteId) {
        List<DireccionCliente> direcciones = new ArrayList<>();
        String sql = "SELECT * FROM DireccionCliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    direcciones.add(mapRowToDireccion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return direcciones;
    }

    public DireccionCliente save(DireccionCliente direccion) {
        String sql = "INSERT INTO DireccionCliente (direccion, ciudad, departamento, esPrincipal, idCliente) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, direccion.getDireccion());
            pstmt.setString(2, direccion.getCiudad());
            pstmt.setString(3, direccion.getDepartamento());
            pstmt.setBoolean(4, direccion.getEsPrincipal());
            pstmt.setInt(5, direccion.getIdCliente());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    direccion.setIdDireccion(generatedKeys.getInt(1));
                    return direccion;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer idDireccion) {
        String sql = "DELETE FROM DireccionCliente WHERE idDireccion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDireccion);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private DireccionCliente mapRowToDireccion(ResultSet rs) throws SQLException {
        return new DireccionCliente(
                rs.getInt("idDireccion"),
                rs.getString("direccion"),
                rs.getString("ciudad"),
                rs.getString("departamento"),
                rs.getBoolean("esPrincipal"),
                rs.getInt("idCliente")
        );
    }
}