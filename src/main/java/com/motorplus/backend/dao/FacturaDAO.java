package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Factura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class FacturaDAO {

    public List<Factura> findAll() {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Factura";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                facturas.add(mapRowToFactura(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }

    public Factura findById(Long id) {
        String sql = "SELECT * FROM Factura WHERE idFactura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFactura(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStatus(Long id, String newStatus) {
        String sql = "UPDATE Factura SET estadoPago = ? WHERE idFactura = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setLong(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Factura save(Factura factura) {
        String sql = "INSERT INTO Factura (fechaEmision, estadoPago, costoManoObra, costoRepuestos, impuestos, valorTotal, idOrdenTrabajo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(factura.getFechaEmision() != null ? factura.getFechaEmision() : LocalDateTime.now()));
            pstmt.setString(2, factura.getEstadoPago() != null ? factura.getEstadoPago() : "Pagada");
            pstmt.setBigDecimal(3, factura.getCostoManoObra());
            pstmt.setBigDecimal(4, factura.getCostoRepuestos());
            pstmt.setBigDecimal(5, factura.getImpuestos());
            pstmt.setBigDecimal(6, factura.getValorTotal());
            pstmt.setLong(7, factura.getIdOrdenTrabajo());

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    factura.setIdFactura(generatedKeys.getLong(1));
                    return factura;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Factura mapRowToFactura(ResultSet rs) throws SQLException {
        Factura factura = new Factura();
        factura.setIdFactura(rs.getLong("idFactura"));

        Timestamp fechaEmision = rs.getTimestamp("fechaEmision");
        if (fechaEmision != null) {
            factura.setFechaEmision(fechaEmision.toLocalDateTime());
        }

        factura.setEstadoPago(rs.getString("estadoPago"));
        factura.setCostoManoObra(rs.getBigDecimal("costoManoObra"));
        factura.setCostoRepuestos(rs.getBigDecimal("costoRepuestos"));
        factura.setImpuestos(rs.getBigDecimal("impuestos"));
        factura.setValorTotal(rs.getBigDecimal("valorTotal"));
        factura.setIdOrdenTrabajo(rs.getLong("idOrdenTrabajo"));
        return factura;
    }

    public Factura findByOrdenId(Long ordenId) {
        String sql = "SELECT * FROM Factura WHERE idOrdenTrabajo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFactura(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Factura> findByEstado(String estado) {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Factura WHERE estadoPago = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facturas.add(mapRowToFactura(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facturas;
    }
}