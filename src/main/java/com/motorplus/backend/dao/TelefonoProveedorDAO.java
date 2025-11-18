package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.TelefonoProveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelefonoProveedorDAO {

    public List<TelefonoProveedor> findByProveedorId(Integer proveedorId) { // Cambiado
        List<TelefonoProveedor> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM TelefonoProveedor WHERE idProveedor = ?"; // Cambiado tabla

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, proveedorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    telefonos.add(mapRowToTelefono(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return telefonos;
    }

    public TelefonoProveedor save(TelefonoProveedor telefono) {
        String sql = "INSERT INTO TelefonoProveedor (numero, tipo, esPrincipal, idProveedor) VALUES (?, ?, ?, ?)"; // Cambiado tabla

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, telefono.getNumero());
            pstmt.setString(2, telefono.getTipo());
            pstmt.setBoolean(3, telefono.getEsPrincipal());
            pstmt.setInt(4, telefono.getIdProveedor()); // Cambiado

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    telefono.setIdTelefono(generatedKeys.getInt(1));
                    return telefono;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer idTelefono) {
        String sql = "DELETE FROM TelefonoProveedor WHERE idTelefono = ?"; // Cambiado tabla

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTelefono);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TelefonoProveedor mapRowToTelefono(ResultSet rs) throws SQLException {
        return new TelefonoProveedor(
                rs.getInt("idTelefono"),
                rs.getString("numero"),
                rs.getString("tipo"),
                rs.getBoolean("esPrincipal"),
                rs.getInt("idProveedor") // Cambiado
        );
    }
}