package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.TelefonoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelefonoClienteDAO {

    public List<TelefonoCliente> findByClienteId(Integer clienteId) {
        List<TelefonoCliente> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM TelefonoCliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
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

    public TelefonoCliente save(TelefonoCliente telefono) {
        String sql = "INSERT INTO TelefonoCliente (numero, tipo, esPrincipal, idCliente) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, telefono.getNumero());
            pstmt.setString(2, telefono.getTipo());
            pstmt.setBoolean(3, telefono.getEsPrincipal());
            pstmt.setInt(4, telefono.getIdCliente());

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
        String sql = "DELETE FROM TelefonoCliente WHERE idTelefono = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTelefono);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TelefonoCliente mapRowToTelefono(ResultSet rs) throws SQLException {
        return new TelefonoCliente(
                rs.getInt("idTelefono"),
                rs.getString("numero"),
                rs.getString("tipo"),
                rs.getBoolean("esPrincipal"),
                rs.getInt("idCliente")
        );
    }
}