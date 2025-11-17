package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Rol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> findAll() {
        String sql = "SELECT * FROM Rol ORDER BY nombre";
        List<Rol> roles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getLong("idRol"));
                rol.setNombre(rs.getString("nombre"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setNivel(rs.getInt("nivel"));

                // Usar el nombre correcto de la columna
                if (rs.getTimestamp("created_at") != null) {
                    rol.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }

                roles.add(rol);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public Rol findById(Long id) {
        String sql = "SELECT * FROM Rol WHERE idRol = ?";
        Rol rol = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rol = new Rol();
                    rol.setIdRol(rs.getLong("idRol"));
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));
                    rol.setNivel(rs.getInt("nivel"));

                    if (rs.getTimestamp("created_at") != null) {
                        rol.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rol;
    }

    public Rol findByNombre(String nombre) {
        String sql = "SELECT * FROM Rol WHERE nombre = ?";
        Rol rol = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rol = new Rol();
                    rol.setIdRol(rs.getLong("idRol"));
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));
                    rol.setNivel(rs.getInt("nivel"));

                    if (rs.getTimestamp("created_at") != null) {
                        rol.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rol;
    }

    public boolean save(Rol rol) {
        String sql = "INSERT INTO Rol (nombre, descripcion, nivel) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, rol.getNombre());
            pstmt.setString(2, rol.getDescripcion());
            pstmt.setInt(3, rol.getNivel());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rol.setIdRol(generatedKeys.getLong(1));
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