package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Proveedor;
import com.motorplus.backend.entity.TelefonoProveedor;
import com.motorplus.backend.entity.CorreoProveedor;
import com.motorplus.backend.entity.DireccionProveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public List<Proveedor> findAll() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM Proveedor";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor proveedor = mapRowToProveedor(rs);
                cargarRelacionesProveedor(proveedor);
                proveedores.add(proveedor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    public Proveedor findById(Integer id) {
        String sql = "SELECT * FROM Proveedor WHERE idProveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Proveedor proveedor = mapRowToProveedor(rs);
                    cargarRelacionesProveedor(proveedor);
                    return proveedor;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Proveedor save(Proveedor proveedor) {
        // CORREGIDO: Agregar campo correo
        String sql = "INSERT INTO Proveedor (nombre, nit, telefono, Correo, direccion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getNit());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getCorreo());  // ← AGREGADO
            pstmt.setString(5, proveedor.getDireccion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear proveedor, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer nuevoId = generatedKeys.getInt(1);
                    proveedor.setIdProveedor(nuevoId);
                    guardarRelacionesProveedor(proveedor);
                    return proveedor;
                } else {
                    throw new SQLException("Error al crear proveedor, no se obtuvo ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Proveedor update(Integer id, Proveedor proveedor) {
        // CORREGIDO: Agregar campo correo
        String sql = "UPDATE Proveedor SET nombre = ?, nit = ?, telefono = ?, Correo = ?, direccion = ? WHERE idProveedor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getNit());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getCorreo());  // ← AGREGADO
            pstmt.setString(5, proveedor.getDireccion());
            pstmt.setInt(6, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                proveedor.setIdProveedor(id);
                actualizarRelacionesProveedor(proveedor);
                return proveedor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteById(Integer id) {
        eliminarRelacionesProveedor(id);

        String sql = "DELETE FROM Proveedor WHERE idProveedor = ?";

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
    private void cargarRelacionesProveedor(Proveedor proveedor) {
        if (proveedor.getIdProveedor() != null) {
            TelefonoProveedorDAO telefonoDAO = new TelefonoProveedorDAO();
            CorreoProveedorDAO correoDAO = new CorreoProveedorDAO();
            DireccionProveedorDAO direccionDAO = new DireccionProveedorDAO();

            proveedor.setTelefonos(telefonoDAO.findByProveedorId(proveedor.getIdProveedor()));
            proveedor.setCorreos(correoDAO.findByProveedorId(proveedor.getIdProveedor()));
            proveedor.setDirecciones(direccionDAO.findByProveedorId(proveedor.getIdProveedor()));
        }
    }

    private void guardarRelacionesProveedor(Proveedor proveedor) {
        if (proveedor.getTelefonos() != null && !proveedor.getTelefonos().isEmpty()) {
            TelefonoProveedorDAO telefonoDAO = new TelefonoProveedorDAO();
            for (TelefonoProveedor telefono : proveedor.getTelefonos()) {
                telefono.setIdProveedor(proveedor.getIdProveedor());
                telefonoDAO.save(telefono);
            }
        }

        if (proveedor.getCorreos() != null && !proveedor.getCorreos().isEmpty()) {
            CorreoProveedorDAO correoDAO = new CorreoProveedorDAO();
            for (CorreoProveedor correo : proveedor.getCorreos()) {
                correo.setIdProveedor(proveedor.getIdProveedor());
                correoDAO.save(correo);
            }
        }

        if (proveedor.getDirecciones() != null && !proveedor.getDirecciones().isEmpty()) {
            DireccionProveedorDAO direccionDAO = new DireccionProveedorDAO();
            for (DireccionProveedor direccion : proveedor.getDirecciones()) {
                direccion.setIdProveedor(proveedor.getIdProveedor());
                direccionDAO.save(direccion);
            }
        }
    }

    private void actualizarRelacionesProveedor(Proveedor proveedor) {
        eliminarRelacionesProveedor(proveedor.getIdProveedor());
        guardarRelacionesProveedor(proveedor);
    }

    private void eliminarRelacionesProveedor(Integer proveedorId) {
        TelefonoProveedorDAO telefonoDAO = new TelefonoProveedorDAO();
        CorreoProveedorDAO correoDAO = new CorreoProveedorDAO();
        DireccionProveedorDAO direccionDAO = new DireccionProveedorDAO();

        List<TelefonoProveedor> telefonos = telefonoDAO.findByProveedorId(proveedorId);
        for (TelefonoProveedor telefono : telefonos) {
            telefonoDAO.delete(telefono.getIdTelefono());
        }

        List<CorreoProveedor> correos = correoDAO.findByProveedorId(proveedorId);
        for (CorreoProveedor correo : correos) {
            correoDAO.delete(correo.getIdCorreo());
        }

        List<DireccionProveedor> direcciones = direccionDAO.findByProveedorId(proveedorId);
        for (DireccionProveedor direccion : direcciones) {
            direccionDAO.delete(direccion.getIdDireccion());
        }
    }

    // CORREGIDO: Incluir campo correo
    private Proveedor mapRowToProveedor(ResultSet rs) throws SQLException {
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(rs.getInt("idProveedor"));
        proveedor.setNombre(rs.getString("nombre"));
        proveedor.setNit(rs.getString("nit"));
        proveedor.setTelefono(rs.getString("telefono"));
        proveedor.setCorreo(rs.getString("Correo"));  // ← AGREGADO
        proveedor.setDireccion(rs.getString("direccion"));

        proveedor.setTelefonos(new ArrayList<>());
        proveedor.setCorreos(new ArrayList<>());
        proveedor.setDirecciones(new ArrayList<>());

        return proveedor;
    }
}