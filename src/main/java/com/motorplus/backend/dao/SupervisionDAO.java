package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Supervision;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupervisionDAO {

    public List<Supervision> findAll() {
        List<Supervision> supervisiones = new ArrayList<>();
        String sql = "SELECT s.*, ms.nombres as nombre_supervisor, md.nombres as nombre_supervisado " +
                "FROM Supervision s " +
                "JOIN Mecanico ms ON s.idMecanicoSupervisor = ms.idMecanico " +
                "JOIN Mecanico md ON s.idMecanicoSupervisado = md.idMecanico " +
                "ORDER BY s.fechaInicio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                supervisiones.add(mapRowToSupervision(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return supervisiones;
    }

    public boolean save(Supervision supervision) {
        String sql = "INSERT INTO Supervision (idMecanicoSupervisor, idMecanicoSupervisado, fechaInicio, fechaFin, observaciones) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, supervision.getIdMecanicoSupervisor());
            pstmt.setLong(2, supervision.getIdMecanicoSupervisado());
            pstmt.setDate(3, Date.valueOf(supervision.getFechaInicio()));

            if (supervision.getFechaFin() != null) {
                pstmt.setDate(4, Date.valueOf(supervision.getFechaFin()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }

            pstmt.setString(5, supervision.getObservaciones());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM Supervision WHERE idSupervision = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Supervision mapRowToSupervision(ResultSet rs) throws SQLException {
        Supervision supervision = new Supervision();
        supervision.setIdSupervision(rs.getLong("idSupervision"));
        supervision.setIdMecanicoSupervisor(rs.getLong("idMecanicoSupervisor"));
        supervision.setIdMecanicoSupervisado(rs.getLong("idMecanicoSupervisado"));
        supervision.setFechaInicio(rs.getDate("fechaInicio").toLocalDate());

        Date fechaFin = rs.getDate("fechaFin");
        if (fechaFin != null) {
            supervision.setFechaFin(fechaFin.toLocalDate());
        }

        supervision.setObservaciones(rs.getString("observaciones"));
        supervision.setNombreSupervisor(rs.getString("nombre_supervisor"));
        supervision.setNombreSupervisado(rs.getString("nombre_supervisado"));

        return supervision;
    }
}