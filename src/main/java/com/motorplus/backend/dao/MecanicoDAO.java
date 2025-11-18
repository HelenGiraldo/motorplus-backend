package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Mecanico;
import com.motorplus.backend.entity.TelefonoMecanico;
import com.motorplus.backend.entity.CorreoMecanico;
import com.motorplus.backend.entity.Especialidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MecanicoDAO {

    public List<Mecanico> findAll() {
        List<Mecanico> mecanicos = new ArrayList<>();

        String sql = "SELECT * FROM Mecanico";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Mecanico mecanico = mapRowToMecanico(rs);
                cargarRelacionesMecanico(mecanico);
                mecanicos.add(mecanico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mecanicos;
    }

    public Mecanico findById(Long id) {
        // QUITAR JOIN con especialidad
        String sql = "SELECT * FROM Mecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Mecanico mecanico = mapRowToMecanico(rs);
                    cargarRelacionesMecanico(mecanico);
                    return mecanico;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Mecanico save(Mecanico mecanico) {
        // QUITAR idEspecialidad del INSERT
        String sql = "INSERT INTO Mecanico (nombres, apellidos, documento, tarifaPorHora, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, mecanico.getNombres());
            pstmt.setString(2, mecanico.getApellidos());
            pstmt.setString(3, mecanico.getDocumento());
            pstmt.setBigDecimal(4, mecanico.getTarifaPorHora());
            pstmt.setString(5, mecanico.getTelefono());
            pstmt.setString(6, mecanico.getCorreo());
            pstmt.setString(7, mecanico.getDireccion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear mecánico, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long nuevoId = generatedKeys.getLong(1);
                    mecanico.setIdMecanico(nuevoId);
                    guardarRelacionesAdicionales(mecanico);
                    return mecanico;
                } else {
                    throw new SQLException("Error al crear mecánico, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Mecanico update(Long id, Mecanico mecanico) {
        // QUITAR idEspecialidad del UPDATE
        String sql = "UPDATE Mecanico SET nombres = ?, apellidos = ?, documento = ?, tarifaPorHora = ?, telefono = ?, correo = ?, direccion = ? WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mecanico.getNombres());
            pstmt.setString(2, mecanico.getApellidos());
            pstmt.setString(3, mecanico.getDocumento());
            pstmt.setBigDecimal(4, mecanico.getTarifaPorHora());
            pstmt.setString(5, mecanico.getTelefono());
            pstmt.setString(6, mecanico.getCorreo());
            pstmt.setString(7, mecanico.getDireccion());
            pstmt.setLong(8, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                mecanico.setIdMecanico(id);
                actualizarRelacionesAdicionales(mecanico);
                return mecanico;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Long id) {
        eliminarRelacionesAdicionales(id);

        String sql = "DELETE FROM Mecanico WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // MÉTODOS AUXILIARES PARA RELACIONES ADICIONALES
    private void cargarRelacionesMecanico(Mecanico mecanico) {
        if (mecanico.getIdMecanico() != null) {
            TelefonoMecanicoDAO telefonoDAO = new TelefonoMecanicoDAO();
            CorreoMecanicoDAO correoDAO = new CorreoMecanicoDAO();

            mecanico.setTelefonos(telefonoDAO.findByMecanicoId(mecanico.getIdMecanico()));
            mecanico.setCorreos(correoDAO.findByMecanicoId(mecanico.getIdMecanico()));
            mecanico.setEspecialidades(findEspecialidadesByMecanicoId(mecanico.getIdMecanico())); // NUEVO
        }
    }

    private void guardarRelacionesAdicionales(Mecanico mecanico) {
        // Teléfonos
        if (mecanico.getTelefonos() != null && !mecanico.getTelefonos().isEmpty()) {
            TelefonoMecanicoDAO telefonoDAO = new TelefonoMecanicoDAO();
            for (TelefonoMecanico telefono : mecanico.getTelefonos()) {
                telefono.setIdMecanico(mecanico.getIdMecanico());
                telefonoDAO.save(telefono);
            }
        }

        // Correos
        if (mecanico.getCorreos() != null && !mecanico.getCorreos().isEmpty()) {
            CorreoMecanicoDAO correoDAO = new CorreoMecanicoDAO();
            for (CorreoMecanico correo : mecanico.getCorreos()) {
                correo.setIdMecanico(mecanico.getIdMecanico());
                correoDAO.save(correo);
            }
        }

        // Especialidades (NUEVO)
        if (mecanico.getEspecialidades() != null && !mecanico.getEspecialidades().isEmpty()) {
            guardarEspecialidadesMecanico(mecanico.getIdMecanico(), mecanico.getEspecialidades());
        }
    }

    private void actualizarRelacionesAdicionales(Mecanico mecanico) {
        eliminarRelacionesAdicionales(mecanico.getIdMecanico());
        guardarRelacionesAdicionales(mecanico);
    }

    private void eliminarRelacionesAdicionales(Long mecanicoId) {
        TelefonoMecanicoDAO telefonoDAO = new TelefonoMecanicoDAO();
        CorreoMecanicoDAO correoDAO = new CorreoMecanicoDAO();

        // Eliminar teléfonos
        List<TelefonoMecanico> telefonos = telefonoDAO.findByMecanicoId(mecanicoId);
        for (TelefonoMecanico telefono : telefonos) {
            telefonoDAO.delete(telefono.getIdTelefono());
        }

        // Eliminar correos
        List<CorreoMecanico> correos = correoDAO.findByMecanicoId(mecanicoId);
        for (CorreoMecanico correo : correos) {
            correoDAO.delete(correo.getIdCorreo());
        }

        // Eliminar especialidades (NUEVO)
        eliminarEspecialidadesMecanico(mecanicoId);
    }

    private Mecanico mapRowToMecanico(ResultSet rs) throws SQLException {
        Mecanico mecanico = new Mecanico();
        mecanico.setIdMecanico(rs.getLong("idMecanico"));
        mecanico.setNombres(rs.getString("nombres"));
        mecanico.setApellidos(rs.getString("apellidos"));
        mecanico.setDocumento(rs.getString("documento"));
        mecanico.setTarifaPorHora(rs.getBigDecimal("tarifaPorHora"));
        mecanico.setTelefono(rs.getString("telefono"));
        mecanico.setCorreo(rs.getString("correo"));
        mecanico.setDireccion(rs.getString("direccion"));

        mecanico.setTelefonos(new ArrayList<>());
        mecanico.setCorreos(new ArrayList<>());
        mecanico.setEspecialidades(new ArrayList<>()); // NUEVO

        return mecanico;
    }

    // MÉTODOS NUEVOS PARA ESPECIALIDADES MÚLTIPLES
    private List<Especialidad> findEspecialidadesByMecanicoId(Long mecanicoId) {
        List<Especialidad> especialidades = new ArrayList<>();
        String sql = "SELECT e.* FROM Especialidad e " +
                "JOIN MecanicoEspecialidad me ON e.idEspecialidad = me.idEspecialidad " +
                "WHERE me.idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, mecanicoId);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return especialidades;
    }

    private void guardarEspecialidadesMecanico(Long mecanicoId, List<Especialidad> especialidades) {
        String sql = "INSERT INTO MecanicoEspecialidad (idMecanico, idEspecialidad) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Especialidad especialidad : especialidades) {
                pstmt.setLong(1, mecanicoId);
                pstmt.setLong(2, especialidad.getIdEspecialidad());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarEspecialidadesMecanico(Long mecanicoId) {
        String sql = "DELETE FROM MecanicoEspecialidad WHERE idMecanico = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, mecanicoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}