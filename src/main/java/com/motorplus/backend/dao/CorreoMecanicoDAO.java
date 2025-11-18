package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.CorreoMecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CorreoMecanicoDAO {

    public List<CorreoMecanico> findByMecanicoId(Long mecanicoId) {
        List<CorreoMecanico> correos = new ArrayList<>();
        String sql = "SELECT * FROM correoMecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, mecanicoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    correos.add(mapRowToCorreo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return correos;
    }

    public CorreoMecanico save(CorreoMecanico correo) {
        // Asegurar que esPrincipal no sea null
        if (correo.getEsPrincipal() == null) {
            correo.setEsPrincipal(false);
        }

        // Verificar si es el primer correo del mecánico
        List<CorreoMecanico> correosExistentes = findByMecanicoId(correo.getIdMecanico());
        boolean esPrimerCorreo = correosExistentes.isEmpty();

        String sql = "INSERT INTO correoMecanico (email, tipo, esPrincipal, idMecanico) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, correo.getEmail());
            pstmt.setString(2, correo.getTipo());
            // Si es el primer correo, se convierte en principal automáticamente
            // Ignoramos el valor que viene del frontend y usamos nuestra lógica
            pstmt.setBoolean(3, esPrimerCorreo);
            pstmt.setLong(4, correo.getIdMecanico());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    correo.setIdCorreo(generatedKeys.getInt(1));
                    correo.setEsPrincipal(esPrimerCorreo);
                    return correo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer idCorreo) {
        // Primero obtener el correo a eliminar para ver si era principal
        CorreoMecanico correoAEliminar = findById(idCorreo);
        boolean eraPrincipal = correoAEliminar != null &&
                correoAEliminar.getEsPrincipal() != null &&
                correoAEliminar.getEsPrincipal();

        String sql = "DELETE FROM correoMecanico WHERE idCorreo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCorreo);
            boolean success = pstmt.executeUpdate() > 0;

            // Si se eliminó un correo principal y hay más correos, hacer principal al primero restante
            if (success && eraPrincipal) {
                promoverPrimerCorreoAPrincipal(correoAEliminar.getIdMecanico());
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para encontrar un correo por ID
    private CorreoMecanico findById(Integer idCorreo) {
        String sql = "SELECT * FROM correoMecanico WHERE idCorreo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCorreo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCorreo(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para promover el primer correo a principal después de eliminar el principal actual
    private void promoverPrimerCorreoAPrincipal(Long mecanicoId) {
        List<CorreoMecanico> correosRestantes = findByMecanicoId(mecanicoId);
        if (!correosRestantes.isEmpty()) {
            // Hacer principal al primer correo de la lista
            CorreoMecanico nuevoPrincipal = correosRestantes.get(0);
            actualizarCorreoPrincipal(nuevoPrincipal.getIdCorreo(), true);
        }
    }

    // Método para actualizar el estado principal de un correo
    private void actualizarCorreoPrincipal(Integer idCorreo, boolean esPrincipal) {
        String sql = "UPDATE correoMecanico SET esPrincipal = ? WHERE idCorreo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, esPrincipal);
            pstmt.setInt(2, idCorreo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CorreoMecanico mapRowToCorreo(ResultSet rs) throws SQLException {
        CorreoMecanico correo = new CorreoMecanico(
                rs.getInt("idCorreo"),
                rs.getString("email"),
                rs.getString("tipo"),
                rs.getBoolean("esPrincipal"),
                rs.getLong("idMecanico")
        );

        // Asegurar que nunca sea null
        if (correo.getEsPrincipal() == null) {
            correo.setEsPrincipal(false);
        }

        return correo;
    }
}