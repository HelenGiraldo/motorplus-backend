package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    private Connection conn = DatabaseConnection.getConnection();

    public List<Servicio> findAll() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicio";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                servicios.add(mapRowToServicio(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }

    public Servicio findById(Long id) {
        String sql = "SELECT * FROM Servicio WHERE idServicio = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToServicio(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Servicio save(Servicio servicio) {
        String sql = "INSERT INTO Servicio (nombre, descripcion, precioBase) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servicio.setIdServicio(generatedKeys.getLong(1));
                    return servicio;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Servicio update(Long id, Servicio servicio) {
        String sql = "UPDATE Servicio SET nombre = ?, descripcion = ?, precioBase = ? WHERE idServicio = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setBigDecimal(3, servicio.getPrecioBase());
            pstmt.setLong(4, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                servicio.setIdServicio(id);
                return servicio;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM Servicio WHERE idServicio = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Servicio mapRowToServicio(ResultSet rs) throws SQLException {
        return new Servicio(
                rs.getLong("idServicio"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBigDecimal("precioBase")
        );
    }
}