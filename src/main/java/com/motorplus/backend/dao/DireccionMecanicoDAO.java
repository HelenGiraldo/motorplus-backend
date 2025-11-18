package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.DireccionMecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DireccionMecanicoDAO {

    public List<DireccionMecanico> findByMecanicoId(Long mecanicoId) {
        List<DireccionMecanico> direcciones = new ArrayList<>();
        String sql = "SELECT * FROM direccionmecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, mecanicoId);
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

    public DireccionMecanico save(DireccionMecanico direccion) {
        String sql = "INSERT INTO direccionmecanico (direccion, ciudad, provincia, codigoPostal, idMecanico) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, direccion.getDireccion());
            pstmt.setString(2, direccion.getCiudad());
            pstmt.setString(3, direccion.getProvincia());
            pstmt.setString(4, direccion.getCodigoPostal());
            pstmt.setLong(5, direccion.getIdMecanico());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    direccion.setIdDireccionMecanico(generatedKeys.getInt(1));
                    return direccion;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer idDireccionMecanico) {
        String sql = "DELETE FROM direccionmecanico WHERE idDireccionMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDireccionMecanico);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private DireccionMecanico mapRowToDireccion(ResultSet rs) throws SQLException {
        DireccionMecanico direccion = new DireccionMecanico();
        direccion.setIdDireccionMecanico(rs.getInt("idDireccionMecanico"));
        direccion.setDireccion(rs.getString("direccion"));
        direccion.setCiudad(rs.getString("ciudad"));
        direccion.setProvincia(rs.getString("provincia"));
        direccion.setCodigoPostal(rs.getString("codigoPostal"));
        direccion.setIdMecanico(rs.getLong("idMecanico"));
        return direccion;
    }
}

