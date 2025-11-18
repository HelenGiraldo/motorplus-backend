package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;
import com.motorplus.backend.entity.OrdenRepuesto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenRepuestoDAO {

    public List<OrdenRepuesto> findByOrdenId(Long ordenId) {
        String sql = "SELECT orp.*, r.nombre as nombreRepuesto, r.descripcion as descripcionRepuesto, " +
                "r.precioVenta as precioUnitario, r.stockDisponible as stockDisponible " +
                "FROM OrdenRepuesto orp " +
                "JOIN Repuesto r ON orp.idRepuesto = r.idRepuesto " +
                "WHERE orp.idOrdenTrabajo = ?";

        List<OrdenRepuesto> repuestos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrdenRepuesto repuesto = new OrdenRepuesto();

                    repuesto.setIdOrden(rs.getLong("idOrdenTrabajo"));
                    repuesto.setIdRepuesto(rs.getLong("idRepuesto"));
                    repuesto.setCantidad(rs.getInt("cantidad"));

                    BigDecimal precioUnitario = rs.getBigDecimal("costoAlMomento");
                    repuesto.setPrecioUnitario(precioUnitario);

                    if (precioUnitario != null && repuesto.getCantidad() != null) {
                        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(repuesto.getCantidad()));
                        repuesto.setSubtotal(subtotal);
                    }

                    repuesto.setNombreRepuesto(rs.getString("nombreRepuesto"));
                    repuesto.setDescripcionRepuesto(rs.getString("descripcionRepuesto"));
                    repuesto.setStockDisponible(rs.getInt("stockDisponible"));

                    repuestos.add(repuesto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repuestos;
    }

    public boolean save(Long ordenId, Long repuestoId, Integer cantidad, BigDecimal precioUnitario) {
        // VALIDACIÓN CRÍTICA: Si el precio es nulo, obtenerlo de la base de datos
        if (precioUnitario == null) {
            precioUnitario = obtenerPrecioRepuesto(repuestoId);
            if (precioUnitario == null) {
                System.out.println("ERROR: No se pudo obtener el precio del repuesto ID: " + repuestoId);
                return false;
            }
        }

        // Verificar stock disponible
        if (!verificarStockDisponible(repuestoId, cantidad)) {
            System.out.println(" Stock insuficiente para el repuesto: " + repuestoId);
            return false;
        }

        // Verificar si ya existe
        if (existeRepuestoEnOrden(ordenId, repuestoId)) {
            return actualizarCantidad(ordenId, repuestoId, cantidad, precioUnitario);
        }

        String sql = "INSERT INTO OrdenRepuesto (idOrdenTrabajo, idRepuesto, cantidad, costoAlMomento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, repuestoId);
            pstmt.setInt(3, cantidad);
            pstmt.setBigDecimal(4, precioUnitario);

            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                actualizarStockRepuesto(repuestoId, -cantidad);
                System.out.println("✅ Repuesto agregado a la orden - ID: " + repuestoId + ", Cantidad: " + cantidad + ", Precio: " + precioUnitario);
            }

            return success;
        } catch (SQLException e) {
            System.out.println("❌ Error al guardar repuesto en orden: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long ordenId, Long repuestoId) {
        Integer cantidad = obtenerCantidadEnOrden(ordenId, repuestoId);

        String sql = "DELETE FROM OrdenRepuesto WHERE idOrdenTrabajo = ? AND idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, repuestoId);

            boolean success = pstmt.executeUpdate() > 0;

            if (success && cantidad != null) {
                actualizarStockRepuesto(repuestoId, cantidad);
                System.out.println("✅ Repuesto eliminado de la orden - ID: " + repuestoId);
            }

            return success;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar repuesto de orden: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // MÉTODO NUEVO PARA OBTENER PRECIO SI ES NULL
    private BigDecimal obtenerPrecioRepuesto(Long repuestoId) {
        String sql = "SELECT precioVenta FROM Repuesto WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, repuestoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal precio = rs.getBigDecimal("precioVenta");
                    System.out.println("💰 Precio obtenido de BD para repuesto " + repuestoId + ": " + precio);
                    return precio;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener precio del repuesto: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private boolean existeRepuestoEnOrden(Long ordenId, Long repuestoId) {
        String sql = "SELECT 1 FROM OrdenRepuesto WHERE idOrdenTrabajo = ? AND idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, repuestoId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean actualizarCantidad(Long ordenId, Long repuestoId, Integer cantidadAgregada, BigDecimal precioUnitario) {

        // Obtener precio si viene null
        if (precioUnitario == null) {
            precioUnitario = obtenerPrecioRepuesto(repuestoId);
            if (precioUnitario == null) {
                System.out.println("❌ ERROR: No se pudo obtener el precio para actualizar repuesto ID: " + repuestoId);
                return false;
            }
        }

        // Cantidad actual en la orden
        Integer cantidadAnterior = obtenerCantidadEnOrden(ordenId, repuestoId);
        if (cantidadAnterior == null) cantidadAnterior = 0;

        // Nueva cantidad = anterior + agregada
        int nuevaCantidad = cantidadAnterior + cantidadAgregada;

        // Actualizar en BD
        String sql = "UPDATE OrdenRepuesto SET cantidad = ?, costoAlMomento = ? WHERE idOrdenTrabajo = ? AND idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, nuevaCantidad);
            pstmt.setBigDecimal(2, precioUnitario);
            pstmt.setLong(3, ordenId);
            pstmt.setLong(4, repuestoId);

            boolean success = pstmt.executeUpdate() > 0;

            if (success) {
                // DESCONTAR del stock solo la nueva cantidad agregada
                actualizarStockRepuesto(repuestoId, -cantidadAgregada);

                System.out.println("🔄 Cantidad actualizada: " + nuevaCantidad + " (se restó " + cantidadAgregada + " del stock)");
            }

            return success;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar cantidad: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    public boolean verificarStockDisponible(Long repuestoId, Integer cantidadRequerida) {
        String sql = "SELECT stockDisponible FROM Repuesto WHERE idRepuesto = ? AND stockDisponible >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, repuestoId);
            pstmt.setInt(2, cantidadRequerida);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getStockDisponible(Long repuestoId) {
        String sql = "SELECT stockDisponible FROM Repuesto WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, repuestoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stockDisponible");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void actualizarStockRepuesto(Long repuestoId, Integer cantidad) {
        String sql = "UPDATE Repuesto SET stockDisponible = stockDisponible + ? WHERE idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cantidad);
            pstmt.setLong(2, repuestoId);
            pstmt.executeUpdate();

            System.out.println(" Stock actualizado - Repuesto: " + repuestoId + ", Cambio: " + cantidad);

        } catch (SQLException e) {
            System.out.println(" Error al actualizar stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer obtenerCantidadEnOrden(Long ordenId, Long repuestoId) {
        String sql = "SELECT cantidad FROM OrdenRepuesto WHERE idOrdenTrabajo = ? AND idRepuesto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, ordenId);
            pstmt.setLong(2, repuestoId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cantidad");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OrdenRepuesto> getRepuestosConStockBajo() {
        String sql = "SELECT r.idRepuesto, r.nombre, r.stockDisponible, r.stockMinimo " +
                "FROM Repuesto r " +
                "WHERE r.stockDisponible <= r.stockMinimo";

        List<OrdenRepuesto> repuestos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrdenRepuesto repuesto = new OrdenRepuesto();
                    repuesto.setIdRepuesto(rs.getLong("idRepuesto"));
                    repuesto.setNombreRepuesto(rs.getString("nombre"));
                    repuesto.setStockDisponible(rs.getInt("stockDisponible"));
                    repuestos.add(repuesto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repuestos;
    }
}