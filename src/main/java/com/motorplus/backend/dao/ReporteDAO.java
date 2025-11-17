package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ReporteDAO {

    private Connection obtenerConexion() throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        System.out.println("Conexión a BD: " + con);
        return con;
    }

    private List<Map<String, Object>> mapearResultados(ResultSet rs) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> fila = new LinkedHashMap<>();
            for (int i = 1; i <= columnas; i++) {
                fila.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            lista.add(fila);
        }
        return lista;
    }

    // ====================================================================================
    // REPORTES SIMPLES
    // ====================================================================================

    public List<Map<String, Object>> getReporteClientes() {
        String sql = "SELECT idCliente, nombres, apellidos, telefono, email FROM Cliente";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteMecanicos() {
        String sql = "SELECT idMecanico, nombres, apellidos, especialidad FROM Mecanico";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteInventario() {
        String sql = "SELECT idRepuesto, nombre, descripcion, costoUnitario as precio, precioVenta, stockDisponible as cantidad, stockMinimo FROM Repuesto";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteFacturasPendientes() {
        String sql = "SELECT idFactura, idOrdenTrabajo, fechaEmision, costoManoObra, costoRepuestos, impuestos, valorTotal, estadoPago FROM Factura WHERE estadoPago = 'Pendiente'";
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteRepuestosUsados() {
        System.out.println("🔍 Ejecutando: getReporteRepuestosUsados");

        // Opción 1: Usando orden_repuesto (snake_case)
        String sql = """
            SELECT 
                r.nombre, 
                COUNT(DISTINCT or_rep.id_orden) AS cantidad_usado,
                SUM(or_rep.cantidad) AS total_cantidad
            FROM orden_repuesto or_rep
            JOIN repuesto r ON r.id_repuesto = or_rep.id_repuesto
            GROUP BY r.nombre
            ORDER BY cantidad_usado DESC;
            """;

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Map<String, Object>> resultado = mapearResultados(rs);
            if (!resultado.isEmpty()) {
                return resultado;
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error con snake_case, intentando camelCase...");
        }

        // Opción 2: Usando OrdenRepuesto (camelCase)
        sql = """
            SELECT 
                r.nombre, 
                COUNT(DISTINCT orp.idOrden) AS cantidad_usado,
                SUM(orp.cantidad) AS total_cantidad
            FROM OrdenRepuesto orp
            JOIN Repuesto r ON r.idRepuesto = orp.idRepuesto
            GROUP BY r.nombre
            ORDER BY cantidad_usado DESC;
            """;

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return mapearResultados(rs);

        } catch (Exception e) {
            System.err.println("❌ Error en getReporteRepuestosUsados:");
            e.printStackTrace();
            return List.of();
        }
    }

    // ====================================================================================
    // REPORTES INTERMEDIOS
    // ====================================================================================

    public List<Map<String, Object>> getReporteOrdenesMes(int anio, int mes) {
        System.out.println("🔍 Ejecutando: getReporteOrdenesMes(" + anio + ", " + mes + ")");

        // Intenta primero con snake_case
        String sql = """
            SELECT 
                ot.id_orden, 
                ot.fecha, 
                ot.estado, 
                ot.costo_total,
                ot.descripcion,
                v.placa,
                v.marca,
                v.modelo,
                c.nombre AS cliente_nombre
            FROM orden_trabajo ot
            LEFT JOIN vehiculo v ON v.placa = ot.placa
            LEFT JOIN cliente c ON c.id_cliente = v.id_cliente
            WHERE YEAR(ot.fecha) = ? AND MONTH(ot.fecha) = ?
            ORDER BY ot.fecha DESC;
            """;

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);

            List<Map<String, Object>> resultado = mapearResultados(ps.executeQuery());
            if (!resultado.isEmpty()) {
                return resultado;
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error con snake_case en getReporteOrdenesMes, intentando camelCase...");
        }

        // Intenta con camelCase si falla
        sql = """
            SELECT 
                ot.idOrden, 
                ot.fecha, 
                ot.estado, 
                ot.costoTotal,
                ot.descripcion,
                v.placa,
                v.marca,
                v.modelo,
                c.nombre AS clienteNombre
            FROM OrdenTrabajo ot
            LEFT JOIN Vehiculo v ON v.placa = ot.placa
            LEFT JOIN Cliente c ON c.idCliente = v.idCliente
            WHERE YEAR(ot.fecha) = ? AND MONTH(ot.fecha) = ?
            ORDER BY ot.fecha DESC;
            """;

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);

            return mapearResultados(ps.executeQuery());

        } catch (Exception e) {
            System.err.println("❌ Error en getReporteOrdenesMes:");
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteHistorialVehiculo(String placa) {
        System.out.println("🔍 Ejecutando historial SIMPLIFICADO para: " + placa);

        // CONSULTA SIMPLIFICADA Y SEGURA
        String sql = """
        SELECT 
            ot.idOrdenTrabajo,
            ot.fechaIngreso,
            ot.diagnosticoInicial,
            ot.estado,
            v.marca,
            v.modelo, 
            v.color,
            CONCAT(c.nombres, ' ', c.apellidos) as cliente,
            f.idFactura,
            f.valorTotal,
            f.estadoPago
        FROM OrdenTrabajo ot
        JOIN Vehiculo v ON ot.idVehiculo = v.idVehiculo
        JOIN Cliente c ON v.idCliente = c.idCliente
        LEFT JOIN Factura f ON f.idOrdenTrabajo = ot.idOrdenTrabajo
        WHERE v.placa = ? AND v.activo = 1
        ORDER BY ot.fechaIngreso DESC;
        """;

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);
            List<Map<String, Object>> resultado = mapearResultados(ps.executeQuery());

            System.out.println("✅ Historial simplificado: " + resultado.size() + " órdenes");

            // Para cada orden, obtener servicios por separado
            for (Map<String, Object> orden : resultado) {
                Long idOrden = (Long) orden.get("idOrdenTrabajo");
                List<String> servicios = obtenerServiciosDeOrden(idOrden);
                orden.put("servicios", servicios.isEmpty() ? "Sin servicios" : String.join(", ", servicios));
            }

            return resultado;

        } catch (Exception e) {
            System.err.println("❌ Error en historial simplificado:");
            e.printStackTrace();
            return List.of();
        }
    }

    private List<String> obtenerServiciosDeOrden(Long idOrden) {
        List<String> servicios = new ArrayList<>();
        String sql = "SELECT s.nombre FROM OrdenServicio os JOIN Servicio s ON os.idServicio = s.idServicio WHERE os.idOrdenTrabajo = ?";

        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idOrden);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                servicios.add(rs.getString("nombre"));
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo servicios para orden " + idOrden);
        }
        return servicios;
    }


    // ====================================================================================
    // REPORTES PARA GRAFICOS (COMPLEJOS)
    // ====================================================================================

    public List<Map<String, Object>> getReporteVentasMes() {
        String sql = """
            SELECT MONTH(fechaEmision) AS mes, SUM(valorTotal) AS total_ventas
            FROM Factura
            WHERE fechaEmision IS NOT NULL AND valorTotal IS NOT NULL
            GROUP BY MONTH(fechaEmision)
            ORDER BY mes;
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteMecanicosProductivos() {
        // CORREGIDO: JOIN con Mecanico para mostrar nombres
        String sql = """
            SELECT m.idMecanico, CONCAT(m.nombres, ' ', m.apellidos) AS nombre_mecanico,
                   COUNT(om.idMecanico) AS ordenes_realizadas
            FROM OrdenMecanico om
            JOIN Mecanico m ON m.idMecanico = om.idMecanico
            GROUP BY m.idMecanico, m.nombres, m.apellidos
            ORDER BY ordenes_realizadas DESC;
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteServiciosPopulares() {
        // CORREGIDO: JOIN con Servicio para mostrar nombres
        String sql = """
            SELECT s.idServicio, s.nombre AS nombre_servicio,
                   COUNT(os.idServicio) AS veces_usado
            FROM OrdenServicio os
            JOIN Servicio s ON s.idServicio = os.idServicio
            GROUP BY s.idServicio, s.nombre
            ORDER BY veces_usado DESC;
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}