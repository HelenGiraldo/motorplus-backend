package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.Factura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class FacturaDAO {

    private Connection conn = DatabaseConnection.getConnection();

    public List<Factura> findAll() {
        List<Factura> facturas = new ArrayList<>();
        String sql = "SELECT * FROM Factura";
        try (Statement stmt = conn.createStatement();
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    private Factura mapRowToFactura(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getLong("idFactura"),
                rs.getTimestamp("fechaEmision").toLocalDateTime(),
                rs.getString("estadoPago"),
                rs.getBigDecimal("costoManoObra"),
                rs.getBigDecimal("costoRepuestos"),
                rs.getBigDecimal("impuestos"),
                rs.getBigDecimal("valorTotal"),
                rs.getLong("idOrdenTrabajo")
        );
    }

    public boolean updateStatus(Long id, String newStatus) {
        String sql = "UPDATE Factura SET estadoPago = ? WHERE idFactura = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(2, "Pagada"); // Asumimos que se paga al facturar
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
}