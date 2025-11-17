package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapRowToCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public Cliente findById(Long id) {
        String sql = "SELECT * FROM Cliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCliente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO Cliente (nombres, apellidos, documento, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getDocumento());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setString(6, cliente.getDireccion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear cliente, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getLong(1));
                    return cliente;
                } else {
                    throw new SQLException("Error al crear cliente, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cliente update(Long id, Cliente cliente) {
        String sql = "UPDATE Cliente SET nombres = ?, apellidos = ?, documento = ?, telefono = ?, email = ?, direccion = ? WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getDocumento());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setString(6, cliente.getDireccion());
            pstmt.setLong(7, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                cliente.setIdCliente(id);
                return cliente;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Cliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Cliente mapRowToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("idCliente"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("documento"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion"),
                null
        );
    }
}
