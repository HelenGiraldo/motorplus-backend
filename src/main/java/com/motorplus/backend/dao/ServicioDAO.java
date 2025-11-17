package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Servicio;
import com.motorplus.backend.entity.TipoServicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ServicioDAO {

    public List<Servicio> findAll() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicio";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

    public List<Servicio> findByTipoServicio(TipoServicio tipoServicio) {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM Servicio WHERE idTipoServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, tipoServicio.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    servicios.add(mapRowToServicio(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }

    public Servicio save(Servicio servicio) {
        String sql = "INSERT INTO Servicio (nombre, descripcion, precioBase, duracionEstimada, tipoServicio, idTipoServicio) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setBigDecimal(3, BigDecimal.valueOf(servicio.getPrecioBase()));
            pstmt.setInt(4, servicio.getDuracionEstimada());
            pstmt.setString(5, servicio.getTipoServicio());

            if (servicio.getIdTipoServicio() != null) {
                pstmt.setLong(6, servicio.getIdTipoServicio());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }

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
        String sql = "UPDATE Servicio SET nombre = ?, descripcion = ?, precioBase = ?, duracionEstimada = ?, tipoServicio = ?, idTipoServicio = ? WHERE idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setBigDecimal(3, BigDecimal.valueOf(servicio.getPrecioBase()));
            pstmt.setInt(4, servicio.getDuracionEstimada());
            pstmt.setString(5, servicio.getTipoServicio());

            if (servicio.getIdTipoServicio() != null) {
                pstmt.setLong(6, servicio.getIdTipoServicio());
            } else {
                pstmt.setNull(6, Types.BIGINT);
            }

            pstmt.setLong(7, id);

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

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Servicio mapRowToServicio(ResultSet rs) throws SQLException {
        Servicio servicio = new Servicio();
        servicio.setIdServicio(rs.getLong("idServicio"));
        servicio.setNombre(rs.getString("nombre"));
        servicio.setDescripcion(rs.getString("descripcion"));
        servicio.setPrecioBase(rs.getDouble("precioBase"));
        servicio.setDuracionEstimada(rs.getInt("duracionEstimada"));

        // SOLUCIÓN: Lee el campo tipoServicio como String (ENUM)
        String tipoServicio = rs.getString("tipoServicio");
        servicio.setTipoServicio(tipoServicio);

        // Lee idTipoServicio si existe
        long idTipoServicio = rs.getLong("idTipoServicio");
        if (!rs.wasNull()) {
            servicio.setIdTipoServicio(idTipoServicio);
        }

        return servicio;
    }
}