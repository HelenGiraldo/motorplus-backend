package com.motorplus.backend.dao;

import com.motorplus.backend.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ReporteDAO {

    private Connection obtenerConexion() throws SQLException {
        return DatabaseConnection.getConnection();
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
    // REPORTES SIMPLES - CORREGIDOS CON NOMBRES REALES
    // ====================================================================================

    public List<Map<String, Object>> getReporteClientes() {
        String sql = """
            SELECT 
                idCliente, 
                nombres, 
                apellidos, 
                documento, 
                telefono, 
                email,
                direccion
            FROM Cliente
            ORDER BY nombres, apellidos
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (SQLException e) {
            System.err.println("❌ Error en reporte clientes: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteMecanicos() {
        String sql = """
            SELECT 
                idMecanico,
                nombres,
                apellidos,
                documento,
                tarifaPorHora,
                telefono,
                correo,
                direccion
            FROM Mecanico
            ORDER BY nombres, apellidos
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println("❌ Error en reporte mecánicos: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteInventario() {
        String sql = """
            SELECT 
                idRepuesto,
                nombre,
                descripcion,
                costoUnitario,
                precioVenta,
                stockDisponible,
                stockMinimo
            FROM Repuesto
            ORDER BY nombre
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println("❌ Error en reporte inventario: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteFacturasPendientes() {
        String sql = """
            SELECT 
                idFactura,
                idOrdenTrabajo,
                fechaEmision,
                costoManoObra,
                costoRepuestos,
                impuestos,
                valorTotal,
                estadoPago
            FROM Factura 
            WHERE estadoPago = 'Pendiente'
            ORDER BY fechaEmision DESC
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println("❌ Error en reporte facturas pendientes: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteRepuestosUsados() {
        String sql = """
            SELECT 
                r.nombre, 
                COUNT(DISTINCT orp.idOrdenTrabajo) AS cantidad_ordenes,
                SUM(orp.cantidad) AS total_cantidad,
                AVG(orp.costoAlMomento) AS precio_promedio
            FROM OrdenRepuesto orp
            JOIN Repuesto r ON r.idRepuesto = orp.idRepuesto
            GROUP BY r.idRepuesto, r.nombre
            ORDER BY total_cantidad DESC
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println("❌ Error en reporte repuestos usados: " + e.getMessage());
            return List.of();
        }
    }

    // ====================================================================================
    // REPORTES INTERMEDIOS
    // ====================================================================================

    public List<Map<String, Object>> getReporteOrdenesMes(int anio, int mes) {
        String sql = """
            SELECT 
                ot.idOrdenTrabajo,
                ot.fechaIngreso,
                ot.diagnosticoInicial,
                ot.estado,
                ot.total AS costoTotal,
                v.placa,
                v.marca,
                v.modelo,
                CONCAT(c.nombres, ' ', c.apellidos) AS cliente_nombre
            FROM OrdenTrabajo ot
            JOIN Vehiculo v ON ot.idVehiculo = v.idVehiculo
            JOIN Cliente c ON v.idCliente = c.idCliente
            WHERE YEAR(ot.fechaIngreso) = ? AND MONTH(ot.fechaIngreso) = ?
            ORDER BY ot.fechaIngreso DESC
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);
            return mapearResultados(ps.executeQuery());

        } catch (Exception e) {
            System.err.println("Error en reporte órdenes mes: " + e.getMessage());
            return List.of();
        }
    }

    // NUEVO MÉTODO PARA DATOS REALES DE GRÁFICA
    public List<Map<String, Object>> getReporteOrdenesMesParaGrafica(int anio, int mes) {
        String sql = """
            SELECT 
                DAY(fechaIngreso) AS dia,
                COUNT(*) AS cantidad_ordenes
            FROM OrdenTrabajo
            WHERE YEAR(fechaIngreso) = ? AND MONTH(fechaIngreso) = ?
                AND fechaIngreso IS NOT NULL
            GROUP BY DAY(fechaIngreso)
            ORDER BY dia
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);
            return mapearResultados(ps.executeQuery());

        } catch (Exception e) {
            System.err.println("Error en reporte órdenes mes para gráfica: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteHistorialVehiculo(String placa) {
        String sql = """
        SELECT 
            ot.idOrdenTrabajo,
            ot.fechaIngreso,
            ot.diagnosticoInicial,
            ot.estado,
            ot.total AS costoTotal,
            v.marca,
            v.modelo,
            v.color,
            v.anio,
            CONCAT(c.nombres, ' ', c.apellidos) AS cliente,
            f.valorTotal,
            f.estadoPago
        FROM OrdenTrabajo ot
        JOIN Vehiculo v ON ot.idVehiculo = v.idVehiculo
        JOIN Cliente c ON v.idCliente = c.idCliente
        LEFT JOIN Factura f ON f.idOrdenTrabajo = ot.idOrdenTrabajo
        WHERE v.placa = ?
        ORDER BY ot.fechaIngreso DESC
        """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);
            List<Map<String, Object>> resultado = mapearResultados(ps.executeQuery());

            // Agregar servicios a cada orden - CORREGIDO EL CASTING
            for (Map<String, Object> orden : resultado) {
                Object idOrdenObj = orden.get("idOrdenTrabajo");
                Long idOrden = null;

                if (idOrdenObj instanceof Integer) {
                    idOrden = ((Integer) idOrdenObj).longValue();
                } else if (idOrdenObj instanceof Long) {
                    idOrden = (Long) idOrdenObj;
                }

                if (idOrden != null) {
                    List<String> servicios = obtenerServiciosDeOrden(idOrden);
                    orden.put("servicios", servicios.isEmpty() ? "Sin servicios" : String.join(", ", servicios));
                }
            }

            return resultado;

        } catch (Exception e) {
            System.err.println("Error en historial vehículo: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteTodosVehiculos() {
        String sql = """
        SELECT 
            v.idVehiculo,
            v.placa,
            v.marca,
            v.modelo,
            v.anio,
            v.color,
            CASE 
                WHEN v.activo = 1 THEN 'Activo'
                ELSE 'Inactivo'
            END AS estado,
            CONCAT(c.nombres, ' ', c.apellidos) AS propietario,
            c.documento AS documento_propietario,
            c.telefono AS telefono_propietario,
            COUNT(ot.idOrdenTrabajo) AS total_ordenes,
            MAX(ot.fechaIngreso) AS ultima_visita
        FROM Vehiculo v
        LEFT JOIN Cliente c ON v.idCliente = c.idCliente
        LEFT JOIN OrdenTrabajo ot ON v.idVehiculo = ot.idVehiculo
        GROUP BY 
            v.idVehiculo, v.placa, v.marca, v.modelo, v.anio, v.color, 
            v.activo, c.nombres, c.apellidos, c.documento, c.telefono
        ORDER BY v.activo DESC, v.marca, v.modelo
        """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte todos vehículos: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteVehiculosActivos() {
        String sql = """
        SELECT 
            v.placa,
            v.marca,
            v.modelo,
            v.anio,
            v.color,
            CONCAT(c.nombres, ' ', c.apellidos) AS propietario,
            c.telefono,
            c.email,
            COUNT(ot.idOrdenTrabajo) AS ordenes_totales,
            SUM(CASE WHEN ot.estado = 'Completada' THEN 1 ELSE 0 END) AS ordenes_completadas,
            MAX(ot.fechaIngreso) AS ultima_visita
        FROM Vehiculo v
        JOIN Cliente c ON v.idCliente = c.idCliente
        LEFT JOIN OrdenTrabajo ot ON v.idVehiculo = ot.idVehiculo
        WHERE v.activo = 1
        GROUP BY 
            v.idVehiculo, v.placa, v.marca, v.modelo, v.anio, v.color,
            c.nombres, c.apellidos, c.telefono, c.email
        ORDER BY ordenes_totales DESC, ultima_visita DESC
        """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte vehículos activos: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteVehiculosInactivos() {
        String sql = """
        SELECT 
            v.placa,
            v.marca,
            v.modelo,
            v.anio,
            v.color,
            CONCAT(c.nombres, ' ', c.apellidos) AS propietario,
            c.telefono,
            c.email,
            COUNT(ot.idOrdenTrabajo) AS ordenes_totales,
            MAX(ot.fechaIngreso) AS ultima_visita
        FROM Vehiculo v
        LEFT JOIN Cliente c ON v.idCliente = c.idCliente
        LEFT JOIN OrdenTrabajo ot ON v.idVehiculo = ot.idVehiculo
        WHERE v.activo = 0
        GROUP BY 
            v.idVehiculo, v.placa, v.marca, v.modelo, v.anio, v.color,
            c.nombres, c.apellidos, c.telefono, c.email
        ORDER BY ultima_visita DESC
        """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte vehículos inactivos: " + e.getMessage());
            return List.of();
        }
    }

    private List<String> obtenerServiciosDeOrden(Long idOrden) {
        List<String> servicios = new ArrayList<>();
        String sql = """
            SELECT s.nombre 
            FROM OrdenServicio os 
            JOIN Servicio s ON os.idServicio = s.idServicio 
            WHERE os.idOrdenTrabajo = ?
            """;
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
    // REPORTES PARA GRAFICOS
    // ====================================================================================

    public List<Map<String, Object>> getReporteVentasMes() {
        String sql = """
            SELECT 
                MONTH(fechaEmision) AS mes,
                YEAR(fechaEmision) AS año,
                COUNT(*) AS cantidad_facturas,
                SUM(valorTotal) AS total_ventas
            FROM Factura
            WHERE fechaEmision IS NOT NULL 
                AND valorTotal IS NOT NULL
                AND estadoPago = 'Pagada'
            GROUP BY YEAR(fechaEmision), MONTH(fechaEmision)
            ORDER BY año, mes
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println("Error en reporte ventas mes: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteMecanicosProductivos() {
        String sql = """
            SELECT 
                m.idMecanico,
                CONCAT(m.nombres, ' ', m.apellidos) AS mecanico,
                COUNT(om.idOrdenTrabajo) AS ordenes_atendidas,
                COALESCE(SUM(om.horasTrabajadas), 0) AS horas_trabajadas,
                COALESCE(SUM(om.manoDeObra), 0) AS total_mano_obra
            FROM OrdenMecanico om
            JOIN Mecanico m ON om.idMecanico = m.idMecanico
            GROUP BY m.idMecanico, m.nombres, m.apellidos
            ORDER BY ordenes_atendidas DESC
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte mecánicos productivos: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteServiciosPopulares() {
        String sql = """
            SELECT 
                s.idServicio,
                s.nombre AS servicio,
                s.descripcion,
                COUNT(os.idOrdenTrabajo) AS veces_solicitado
            FROM OrdenServicio os
            JOIN Servicio s ON os.idServicio = s.idServicio
            GROUP BY s.idServicio, s.nombre, s.descripcion
            ORDER BY veces_solicitado DESC
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte servicios populares: " + e.getMessage());
            return List.of();
        }
    }

    public List<Map<String, Object>> getReporteEficienciaTaller() {
        String sql = """
            SELECT 
                estado,
                COUNT(*) AS cantidad,
                ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM OrdenTrabajo)), 2) AS porcentaje
            FROM OrdenTrabajo
            GROUP BY estado
            ORDER BY 
                CASE estado
                    WHEN 'Completada' THEN 1
                    WHEN 'En Proceso' THEN 2
                    WHEN 'Pendiente' THEN 3
                    WHEN 'Cancelada' THEN 4
                    ELSE 5
                END
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte eficiencia taller: " + e.getMessage());
            return List.of();
        }
    }

    // ====================================================================================
    // REPORTES ADICIONALES ÚTILES
    // ====================================================================================

    public List<Map<String, Object>> getReporteIngresosMensuales() {
        String sql = """
            SELECT 
                YEAR(fechaEmision) AS año,
                MONTH(fechaEmision) AS mes,
                SUM(costoManoObra) AS total_mano_obra,
                SUM(costoRepuestos) AS total_repuestos,
                SUM(impuestos) AS total_impuestos,
                SUM(valorTotal) AS total_general
            FROM Factura
            WHERE estadoPago = 'Pagada'
                AND fechaEmision IS NOT NULL
            GROUP BY YEAR(fechaEmision), MONTH(fechaEmision)
            ORDER BY año DESC, mes DESC
            """;
        try (Connection con = obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapearResultados(rs);
        } catch (Exception e) {
            System.err.println(" Error en reporte ingresos mensuales: " + e.getMessage());
            return List.of();
        }
    }
}