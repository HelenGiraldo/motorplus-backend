package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.TelefonoMecanico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelefonoMecanicoDAO {

    public List<TelefonoMecanico> findByMecanicoId(Long mecanicoId) {
        List<TelefonoMecanico> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM telefonoMecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, mecanicoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    telefonos.add(mapRowToTelefono(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return telefonos;
    }

    public TelefonoMecanico save(TelefonoMecanico telefono) {
        // Asegurar que esPrincipal no sea null
        if (telefono.getEsPrincipal() == null) {
            telefono.setEsPrincipal(false);
        }

        // Verificar si es el primer teléfono del mecánico
        List<TelefonoMecanico> telefonosExistentes = findByMecanicoId(telefono.getIdMecanico());
        boolean esPrimerTelefono = telefonosExistentes.isEmpty();

        String sql = "INSERT INTO telefonoMecanico (numero, tipo, esPrincipal, idMecanico) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, telefono.getNumero());
            pstmt.setString(2, telefono.getTipo());
            // Si es el primer teléfono, se convierte en principal automáticamente
            // Ignoramos el valor que viene del frontend y usamos nuestra lógica
            pstmt.setBoolean(3, esPrimerTelefono);
            pstmt.setLong(4, telefono.getIdMecanico());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    telefono.setIdTelefono(generatedKeys.getInt(1));
                    telefono.setEsPrincipal(esPrimerTelefono);
                    return telefono;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean delete(Integer idTelefono) {
        // Primero obtener el teléfono a eliminar para ver si era principal
        TelefonoMecanico telefonoAEliminar = findById(idTelefono);
        boolean eraPrincipal = telefonoAEliminar != null &&
                telefonoAEliminar.getEsPrincipal() != null &&
                telefonoAEliminar.getEsPrincipal();

        String sql = "DELETE FROM telefonoMecanico WHERE idTelefono = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTelefono);
            boolean success = pstmt.executeUpdate() > 0;

            // Si se eliminó un teléfono principal y hay más teléfonos, hacer principal al primero restante
            if (success && eraPrincipal) {
                promoverPrimerTelefonoAPrincipal(telefonoAEliminar.getIdMecanico());
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para encontrar un teléfono por ID
    private TelefonoMecanico findById(Integer idTelefono) {
        String sql = "SELECT * FROM telefonoMecanico WHERE idTelefono = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idTelefono);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTelefono(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para promover el primer teléfono a principal después de eliminar el principal actual
    private void promoverPrimerTelefonoAPrincipal(Long mecanicoId) {
        List<TelefonoMecanico> telefonosRestantes = findByMecanicoId(mecanicoId);
        if (!telefonosRestantes.isEmpty()) {
            // Hacer principal al primer teléfono de la lista
            TelefonoMecanico nuevoPrincipal = telefonosRestantes.get(0);
            actualizarTelefonoPrincipal(nuevoPrincipal.getIdTelefono(), true);
        }
    }

    // Método para actualizar el estado principal de un teléfono
    private void actualizarTelefonoPrincipal(Integer idTelefono, boolean esPrincipal) {
        String sql = "UPDATE telefonoMecanico SET esPrincipal = ? WHERE idTelefono = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, esPrincipal);
            pstmt.setInt(2, idTelefono);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private TelefonoMecanico mapRowToTelefono(ResultSet rs) throws SQLException {
        TelefonoMecanico telefono = new TelefonoMecanico(
                rs.getInt("idTelefono"),
                rs.getString("numero"),
                rs.getString("tipo"),
                rs.getBoolean("esPrincipal"),
                rs.getLong("idMecanico")
        );

        // Asegurar que nunca sea null
        if (telefono.getEsPrincipal() == null) {
            telefono.setEsPrincipal(false);
        }

        return telefono;
    }
}