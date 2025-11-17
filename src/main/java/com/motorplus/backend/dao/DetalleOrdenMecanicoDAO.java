package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.DetalleOrdenMecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleOrdenMecanicoDAO {

    public List<DetalleOrdenMecanico> findByOrdenId(Long ordenId) {
        String sql = "SELECT om.*, m.nombres, m.apellidos, s.nombre as servicio_nombre, r.nombre as rol_nombre " +
                "FROM OrdenMecanico om " +
                "JOIN Mecanico m ON om.idMecanico = m.idMecanico " +
                "JOIN Servicio s ON om.idServicio = s.idServicio " +
                "LEFT JOIN Rol r ON om.idRol = r.idRol " +
                "WHERE om.idOrdenTrabajo = ?";

        List<DetalleOrdenMecanico> detalles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetalleOrdenMecanico detalle = new DetalleOrdenMecanico();
                    detalle.setIdOrden(rs.getLong("idOrdenTrabajo"));
                    detalle.setIdMecanico(rs.getLong("idMecanico"));
                    detalle.setIdServicio(rs.getLong("idServicio"));
                    detalle.setIdRol(rs.getLong("idRol"));
                    detalle.setHorasTrabajadas(rs.getInt("horasTrabajadas"));
                    detalle.setManoDeObra(rs.getDouble("manoDeObra"));

                    // Datos adicionales para la UI
                    detalle.setNombreMecanico(rs.getString("nombres") + " " + rs.getString("apellidos"));
                    detalle.setNombreServicio(rs.getString("servicio_nombre"));
                    detalle.setNombreRol(rs.getString("rol_nombre"));

                    detalles.add(detalle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    public boolean asignarMecanico(DetalleOrdenMecanico detalle) {
        String sql = "INSERT INTO OrdenMecanico (idOrdenTrabajo, idMecanico, idServicio, idRol, horasTrabajadas, manoDeObra) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, detalle.getIdOrden());
            pstmt.setLong(2, detalle.getIdMecanico());
            pstmt.setLong(3, detalle.getIdServicio());

            if (detalle.getIdRol() != null) {
                pstmt.setLong(4, detalle.getIdRol());
            } else {
                pstmt.setNull(4, java.sql.Types.BIGINT);
            }

            pstmt.setInt(5, detalle.getHorasTrabajadas());
            pstmt.setDouble(6, detalle.getManoDeObra());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarAsignacion(Long ordenId, Long mecanicoId, Long servicioId) {
        String sql = "DELETE FROM OrdenMecanico WHERE idOrdenTrabajo = ? AND idMecanico = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, mecanicoId);
            pstmt.setLong(3, servicioId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarHorasYManoObra(Long ordenId, Long mecanicoId, Long servicioId, Integer horas, Double manoObra) {
        String sql = "UPDATE OrdenMecanico SET horasTrabajadas = ?, manoDeObra = ? WHERE idOrdenTrabajo = ? AND idMecanico = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, horas);
            pstmt.setDouble(2, manoObra);
            pstmt.setLong(3, ordenId);
            pstmt.setLong(4, mecanicoId);
            pstmt.setLong(5, servicioId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}