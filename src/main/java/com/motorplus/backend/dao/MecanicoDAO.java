package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Mecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MecanicoDAO {

    public List<Mecanico> findAll() {
        List<Mecanico> mecanicos = new ArrayList<>();
        String sql = "SELECT * FROM Mecanico";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                mecanicos.add(mapRowToMecanico(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mecanicos;
    }

    public Mecanico findById(Long id) {
        String sql = "SELECT * FROM Mecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMecanico(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Mecanico save(Mecanico mecanico) {
        String sql = "INSERT INTO Mecanico (nombres, apellidos, documento, especialidad) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, mecanico.getNombres());
            pstmt.setString(2, mecanico.getApellidos());
            pstmt.setString(3, mecanico.getDocumento());
            pstmt.setString(4, mecanico.getEspecialidad());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mecanico.setIdMecanico(generatedKeys.getLong(1));
                    return mecanico;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Mecanico update(Long id, Mecanico mecanico) {
        String sql = "UPDATE Mecanico SET nombres = ?, apellidos = ?, documento = ?, especialidad = ? WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mecanico.getNombres());
            pstmt.setString(2, mecanico.getApellidos());
            pstmt.setString(3, mecanico.getDocumento());
            pstmt.setString(4, mecanico.getEspecialidad());
            pstmt.setLong(5, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                mecanico.setIdMecanico(id);
                return mecanico;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Mecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Mecanico mapRowToMecanico(ResultSet rs) throws SQLException {
        return new Mecanico(
                rs.getLong("idMecanico"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("documento"),
                rs.getString("especialidad")
        );
    }
}
