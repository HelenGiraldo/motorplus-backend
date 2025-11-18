package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Cliente;
import com.motorplus.backend.entity.TelefonoCliente;
import com.motorplus.backend.entity.CorreoCliente;
import com.motorplus.backend.entity.DireccionCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = mapRowToCliente(rs);

                // DIAGNÓSTICO
                System.out.println("Cargando cliente ID: " + cliente.getIdCliente());
                diagnosticarRelaciones(cliente.getIdCliente());

                // CARGAR RELACIONES PARA CADA CLIENTE
                cargarRelacionesCliente(cliente);

                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    public Cliente findById(Long id) {
        String sql = "SELECT * FROM Cliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapRowToCliente(rs);

                    // CARGAR RELACIONES
                    cargarRelacionesCliente(cliente);

                    return cliente;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO Cliente (nombres, apellidos, documento, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getDocumento());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setString(6, cliente.getDireccion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear cliente, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer nuevoId = generatedKeys.getInt(1);
                    cliente.setIdCliente(nuevoId);

                    // GUARDAR RELACIONES SI EXISTEN
                    guardarRelacionesCliente(cliente);

                    return cliente;
                } else {
                    throw new SQLException("Error al crear cliente, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cliente update(Integer id, Cliente cliente) {
        String sql = "UPDATE Cliente SET nombres = ?, apellidos = ?, documento = ?, telefono = ?, email = ?, direccion = ? WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getNombres());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getDocumento());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getEmail());
            pstmt.setString(6, cliente.getDireccion());
            pstmt.setInt(7, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                cliente.setIdCliente(id);

                // ACTUALIZAR RELACIONES
                actualizarRelacionesCliente(cliente);

                return cliente;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Integer id) {
        // Primero eliminar relaciones
        eliminarRelacionesCliente(id);

        // Luego eliminar cliente
        String sql = "DELETE FROM Cliente WHERE idCliente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // MÉTODOS AUXILIARES PARA RELACIONES

    private void cargarRelacionesCliente(Cliente cliente) {
        if (cliente.getIdCliente() != null) {
            TelefonoClienteDAO telefonoDAO = new TelefonoClienteDAO();
            CorreoClienteDAO correoDAO = new CorreoClienteDAO();
            DireccionClienteDAO direccionDAO = new DireccionClienteDAO();

            cliente.setTelefonos(telefonoDAO.findByClienteId(cliente.getIdCliente()));
            cliente.setCorreos(correoDAO.findByClienteId(cliente.getIdCliente()));
            cliente.setDirecciones(direccionDAO.findByClienteId(cliente.getIdCliente()));
        }
    }

    private void guardarRelacionesCliente(Cliente cliente) {
        if (cliente.getTelefonos() != null && !cliente.getTelefonos().isEmpty()) {
            TelefonoClienteDAO telefonoDAO = new TelefonoClienteDAO();
            for (TelefonoCliente telefono : cliente.getTelefonos()) {
                telefono.setIdCliente(cliente.getIdCliente());
                telefonoDAO.save(telefono);
            }
        }

        if (cliente.getCorreos() != null && !cliente.getCorreos().isEmpty()) {
            CorreoClienteDAO correoDAO = new CorreoClienteDAO();
            for (CorreoCliente correo : cliente.getCorreos()) {
                correo.setIdCliente(cliente.getIdCliente());
                correoDAO.save(correo);
            }
        }

        if (cliente.getDirecciones() != null && !cliente.getDirecciones().isEmpty()) {
            DireccionClienteDAO direccionDAO = new DireccionClienteDAO();
            for (DireccionCliente direccion : cliente.getDirecciones()) {
                direccion.setIdCliente(cliente.getIdCliente());
                direccionDAO.save(direccion);
            }
        }
    }

    private void actualizarRelacionesCliente(Cliente cliente) {
        // Primero eliminar relaciones existentes
        eliminarRelacionesCliente(cliente.getIdCliente());

        // Luego guardar las nuevas relaciones
        guardarRelacionesCliente(cliente);
    }

    private void eliminarRelacionesCliente(Integer clienteId) {
        TelefonoClienteDAO telefonoDAO = new TelefonoClienteDAO();
        CorreoClienteDAO correoDAO = new CorreoClienteDAO();
        DireccionClienteDAO direccionDAO = new DireccionClienteDAO();

        // Eliminar todos los teléfonos del cliente
        List<TelefonoCliente> telefonos = telefonoDAO.findByClienteId(clienteId);
        for (TelefonoCliente telefono : telefonos) {
            telefonoDAO.delete(telefono.getIdTelefono());
        }

        // Eliminar todos los correos del cliente
        List<CorreoCliente> correos = correoDAO.findByClienteId(clienteId);
        for (CorreoCliente correo : correos) {
            correoDAO.delete(correo.getIdCorreo());
        }

        // Eliminar todas las direcciones del cliente
        List<DireccionCliente> direcciones = direccionDAO.findByClienteId(clienteId);
        for (DireccionCliente direccion : direcciones) {
            direccionDAO.delete(direccion.getIdDireccion());
        }
    }

    private Cliente mapRowToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setNombres(rs.getString("nombres"));
        cliente.setApellidos(rs.getString("apellidos"));
        cliente.setDocumento(rs.getString("documento"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setEmail(rs.getString("email"));
        cliente.setDireccion(rs.getString("direccion"));

        // Las relaciones se cargarán después
        cliente.setTelefonos(new ArrayList<>());
        cliente.setCorreos(new ArrayList<>());
        cliente.setDirecciones(new ArrayList<>());
        cliente.setVehiculos(new ArrayList<>());

        return cliente;
    }

    // MÉTODO DE DIAGNÓSTICO
    private void diagnosticarRelaciones(Integer clienteId) {
        System.out.println("=== DIAGNÓSTICO RELACIONES CLIENTE " + clienteId + " ===");

        try {
            TelefonoClienteDAO telefonoDAO = new TelefonoClienteDAO();
            CorreoClienteDAO correoDAO = new CorreoClienteDAO();
            DireccionClienteDAO direccionDAO = new DireccionClienteDAO();

            List<TelefonoCliente> telefonos = telefonoDAO.findByClienteId(clienteId);
            List<CorreoCliente> correos = correoDAO.findByClienteId(clienteId);
            List<DireccionCliente> direcciones = direccionDAO.findByClienteId(clienteId);

            System.out.println("Teléfonos encontrados: " + telefonos.size());
            for (TelefonoCliente t : telefonos) {
                System.out.println("  - " + t.getNumero() + " (" + t.getTipo() + ")");
            }

            System.out.println("Correos encontrados: " + correos.size());
            for (CorreoCliente c : correos) {
                System.out.println("  - " + c.getEmail() + " (" + c.getTipo() + ")");
            }

            System.out.println("Direcciones encontradas: " + direcciones.size());
            for (DireccionCliente d : direcciones) {
                System.out.println("  - " + d.getDireccion() + ", " + d.getCiudad());
            }
            System.out.println("=== FIN DIAGNÓSTICO ===");
        } catch (Exception e) {
            System.out.println("ERROR en diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}