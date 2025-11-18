package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.DetalleOrdenMecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleOrdenMecanicoDAO {

    public List<DetalleOrdenMecanico> findByOrdenId(Long ordenId) {
        String sql = "SELECT om.idOrdenTrabajo, om.idMecanico, om.idServicio, om.idRol, " +
                "om.horasTrabajadas, om.manoDeObra, " +
                "m.nombres, m.apellidos, m.tarifaPorHora, " +
                "s.nombre as servicio_nombre, " +
                "COALESCE(r.nombre, 'Sin rol') as rol_nombre " +
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

                    long idRol = rs.getLong("idRol");
                    if (!rs.wasNull()) {
                        detalle.setIdRol(idRol);
                    }

                    detalle.setHorasTrabajadas(rs.getInt("horasTrabajadas"));
                    detalle.setManoDeObra(rs.getDouble("manoDeObra"));
                    detalle.setTarifaPorHora(rs.getDouble("tarifaPorHora"));

                    detalle.setNombreMecanico(
                            rs.getString("nombres") + " " + rs.getString("apellidos")
                    );
                    detalle.setNombreServicio(rs.getString("servicio_nombre"));
                    detalle.setNombreRol(rs.getString("rol_nombre"));

                    detalles.add(detalle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener mecánicos de la orden: " + e.getMessage(), e);
        }
        return detalles;
    }

    public boolean asignarMecanico(DetalleOrdenMecanico detalle) {
        // Verificar si ya existe esta asignación
        if (existeAsignacion(detalle.getIdOrden(), detalle.getIdMecanico(), detalle.getIdServicio())) {
            System.out.println("⚠️ El mecánico ID " + detalle.getIdMecanico() +
                    " ya está asignado al servicio ID " + detalle.getIdServicio() +
                    " en la orden ID " + detalle.getIdOrden());
            return false;
        }

        Double tarifaPorHora = obtenerTarifaMecanico(detalle.getIdMecanico());
        if (tarifaPorHora == null || tarifaPorHora == 0.0) {
            System.out.println("❌ No se pudo obtener la tarifa del mecánico: " + detalle.getIdMecanico());
            return false;
        }

        Double manoDeObraCalculada = tarifaPorHora * detalle.getHorasTrabajadas();

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
            pstmt.setDouble(6, manoDeObraCalculada);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("✅ Mecánico asignado - Filas afectadas: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Código de error para duplicados en MySQL
                System.out.println("❌ Error: El mecánico ya está asignado a este servicio en la orden");
                return false;
            }
            System.out.println("❌ Error SQL en asignarMecanico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // MÉTODO ÚNICO - ELIMINAR EL DUPLICADO
    public boolean existeAsignacion(Long ordenId, Long mecanicoId, Long servicioId) {
        String sql = "SELECT 1 FROM OrdenMecanico WHERE idOrdenTrabajo = ? AND idMecanico = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, mecanicoId);
            pstmt.setLong(3, servicioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean existe = rs.next();
                if (existe) {
                    System.out.println("🔍 Ya existe asignación: Orden=" + ordenId +
                            ", Mecánico=" + mecanicoId + ", Servicio=" + servicioId);
                }
                return existe;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarAsignacion(Long ordenId, Long mecanicoId, Long servicioId) {
        String sql = "DELETE FROM OrdenMecanico WHERE idOrdenTrabajo = ? AND idMecanico = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, ordenId);
            stmt.setLong(2, mecanicoId);
            stmt.setLong(3, servicioId);

            int affectedRows = stmt.executeUpdate();
            System.out.println("Filas afectadas al eliminar: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error en eliminarAsignacion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarHorasYManoObra(Long ordenId, Long mecanicoId, Long servicioId, Integer horas, Double manoObra) {
        Double tarifaPorHora = obtenerTarifaMecanico(mecanicoId);
        Double manoDeObraCalculada = tarifaPorHora * horas;

        String sql = "UPDATE OrdenMecanico SET horasTrabajadas = ?, manoDeObra = ? WHERE idOrdenTrabajo = ? AND idMecanico = ? AND idServicio = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, horas);
            pstmt.setDouble(2, manoDeObraCalculada);
            pstmt.setLong(3, ordenId);
            pstmt.setLong(4, mecanicoId);
            pstmt.setLong(5, servicioId);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Filas afectadas al actualizar horas: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error en actualizarHorasYManoObra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Double obtenerTarifaMecanico(Long mecanicoId) {
        String sql = "SELECT tarifaPorHora FROM Mecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, mecanicoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("tarifaPorHora");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}