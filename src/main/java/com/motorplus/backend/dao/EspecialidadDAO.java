package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Especialidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAO {

    public List<Especialidad> findAll() {
        String sql = "SELECT * FROM Especialidad ORDER BY nombre";
        List<Especialidad> especialidades = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Especialidad especialidad = new Especialidad();
                especialidad.setIdEspecialidad(rs.getLong("idEspecialidad"));
                especialidad.setNombre(rs.getString("nombre"));
                especialidad.setDescripcion(rs.getString("descripcion"));
                especialidad.setNivel(rs.getInt("nivel"));

                if (rs.getTimestamp("created_at") != null) {
                    especialidad.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }

                especialidades.add(especialidad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return especialidades;
    }

    public Especialidad findById(Long id) {
        String sql = "SELECT * FROM Especialidad WHERE idEspecialidad = ?";
        Especialidad especialidad = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    especialidad = new Especialidad();
                    especialidad.setIdEspecialidad(rs.getLong("idEspecialidad"));
                    especialidad.setNombre(rs.getString("nombre"));
                    especialidad.setDescripcion(rs.getString("descripcion"));
                    especialidad.setNivel(rs.getInt("nivel"));

                    if (rs.getTimestamp("created_at") != null) {
                        especialidad.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return especialidad;
    }

    public Especialidad findByNombre(String nombre) {
        String sql = "SELECT * FROM Especialidad WHERE nombre = ?";
        Especialidad especialidad = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    especialidad = new Especialidad();
                    especialidad.setIdEspecialidad(rs.getLong("idEspecialidad"));
                    especialidad.setNombre(rs.getString("nombre"));
                    especialidad.setDescripcion(rs.getString("descripcion"));
                    especialidad.setNivel(rs.getInt("nivel"));

                    if (rs.getTimestamp("created_at") != null) {
                        especialidad.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return especialidad;
    }

    public boolean save(Especialidad especialidad) {
        String sql = "INSERT INTO Especialidad (nombre, descripcion, nivel) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, especialidad.getNombre());
            pstmt.setString(2, especialidad.getDescripcion());
            pstmt.setInt(3, especialidad.getNivel());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        especialidad.setIdEspecialidad(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
