package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Vehiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    public List<Vehiculo> findAll() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM Vehiculo WHERE activo = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(mapRowToVehiculo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    public Vehiculo findById(Long id) {
        String sql = "SELECT * FROM Vehiculo WHERE idVehiculo = ? AND activo = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToVehiculo(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Vehiculo> findByClienteId(Long clienteId) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM Vehiculo WHERE idCliente = ? AND activo = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, clienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehiculos.add(mapRowToVehiculo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    public Vehiculo save(Vehiculo vehiculo) {
        String sql = "INSERT INTO Vehiculo (placa, marca, modelo, anio, color, idCliente, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, vehiculo.getPlaca());
            pstmt.setString(2, vehiculo.getMarca());
            pstmt.setString(3, vehiculo.getModelo());
            pstmt.setInt(4, vehiculo.getAnio());
            pstmt.setString(5, vehiculo.getColor());

            if (vehiculo.getIdCliente() == null || vehiculo.getIdCliente() == 0) {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            } else {
                pstmt.setLong(6, vehiculo.getIdCliente());
            }

            pstmt.setBoolean(7, true); // Siempre crear como activo

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehiculo.setIdVehiculo(generatedKeys.getLong(1));
                    return vehiculo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vehiculo update(Long id, Vehiculo vehiculo) {
        String sql = "UPDATE Vehiculo SET placa = ?, marca = ?, modelo = ?, anio = ?, idCliente = ?, color = ? WHERE idVehiculo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehiculo.getPlaca());
            pstmt.setString(2, vehiculo.getMarca());
            pstmt.setString(3, vehiculo.getModelo());
            pstmt.setInt(4, vehiculo.getAnio());

            if (vehiculo.getIdCliente() == null || vehiculo.getIdCliente() == 0) {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                pstmt.setLong(5, vehiculo.getIdCliente());
            }

            if (vehiculo.getColor() == null || vehiculo.getColor().trim().isEmpty()) {
                pstmt.setNull(6, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(6, vehiculo.getColor());
            }

            pstmt.setLong(7, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                vehiculo.setIdVehiculo(id);
                return vehiculo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "UPDATE Vehiculo SET activo = 0 WHERE idVehiculo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Vehiculo> findAllIncludingInactive() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        String sql = "SELECT * FROM Vehiculo";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(mapRowToVehiculo(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    // MÉTODO PARA CAMBIAR ESTADO ACTIVO/INACTIVO
    public boolean cambiarEstado(Long id, boolean activo) {
        String sql = "UPDATE Vehiculo SET activo = ? WHERE idVehiculo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, activo);
            pstmt.setLong(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Vehiculo mapRowToVehiculo(ResultSet rs) throws SQLException {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(rs.getLong("idVehiculo"));
        vehiculo.setPlaca(rs.getString("placa"));
        vehiculo.setMarca(rs.getString("marca"));
        vehiculo.setModelo(rs.getString("modelo"));
        vehiculo.setAnio(rs.getInt("anio"));

        // Manejar idCliente que puede ser NULL
        long idCliente = rs.getLong("idCliente");
        vehiculo.setIdCliente(rs.wasNull() ? null : idCliente);

        vehiculo.setColor(rs.getString("color"));
        vehiculo.setActivo(rs.getBoolean("activo"));

        return vehiculo;
    }
}