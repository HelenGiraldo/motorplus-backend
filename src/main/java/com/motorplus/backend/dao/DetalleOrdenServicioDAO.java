package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.DetalleOrdenServicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleOrdenServicioDAO {

    public List<DetalleOrdenServicio> findByOrdenId(Long ordenId) {
        // CORREGIDO: Usar los nombres correctos de columnas
        String sql = "SELECT os.*, s.nombre as nombre_servicio, s.descripcion as descripcion_servicio, " +
                "s.precioBase, s.tipoServicio as nombre_tipo_servicio " +
                "FROM OrdenServicio os " +
                "JOIN Servicio s ON os.idServicio = s.idServicio " +
                "WHERE os.idOrdenTrabajo = ?";  // Cambiado: idOrden → idOrdenTrabajo

        List<DetalleOrdenServicio> detalles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleOrdenServicio detalle = new DetalleOrdenServicio();
                    detalle.setIdOrden(rs.getLong("idOrdenTrabajo"));  // Cambiado
                    detalle.setIdServicio(rs.getLong("idServicio"));
                    detalle.setDescripcionTrabajo(rs.getString("descripcionTrabajo"));

                    // Datos del servicio
                    detalle.setNombreServicio(rs.getString("nombre_servicio"));
                    detalle.setDescripcionServicio(rs.getString("descripcion_servicio"));
                    detalle.setPrecioBase(rs.getDouble("precioBase"));
                    detalle.setNombreTipoServicio(rs.getString("nombre_tipo_servicio"));

                    detalles.add(detalle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    public boolean agregarServicioAOrden(DetalleOrdenServicio detalle) {
        // CORREGIDO: Usar nombres correctos de columnas
        String sql = "INSERT INTO OrdenServicio (idOrdenTrabajo, idServicio, descripcionTrabajo) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, detalle.getIdOrden());
            pstmt.setLong(2, detalle.getIdServicio());

            if (detalle.getDescripcionTrabajo() != null) {
                pstmt.setString(3, detalle.getDescripcionTrabajo());
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarServicioDeOrden(Long ordenId, Long servicioId) {
        // CORREGIDO: Usar nombres correctos de columnas
        String sql = "DELETE FROM OrdenServicio WHERE idOrdenTrabajo = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, servicioId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeServicioEnOrden(Long ordenId, Long servicioId) {
        // CORREGIDO: Usar nombres correctos de columnas
        String sql = "SELECT 1 FROM OrdenServicio WHERE idOrdenTrabajo = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, servicioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}