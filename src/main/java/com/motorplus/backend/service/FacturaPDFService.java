package com.motorplus.backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.motorplus.backend.dao.OrdenTrabajoDAO;
import com.motorplus.backend.dao.VehiculoDAO;
import com.motorplus.backend.dao.ClienteDAO;
import com.motorplus.backend.entity.Factura;
import com.motorplus.backend.entity.OrdenTrabajo;
import com.motorplus.backend.entity.Vehiculo;
import com.motorplus.backend.entity.Cliente;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@Service
public class FacturaPDFService {

    private final OrdenTrabajoDAO ordenTrabajoDAO = new OrdenTrabajoDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public byte[] generarFacturaPDF(Factura factura) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);

            PdfWriter.getInstance(document, outputStream);
            document.open();

            OrdenTrabajo orden = ordenTrabajoDAO.findById(factura.getIdOrdenTrabajo());
            Vehiculo vehiculo = null;
            Cliente cliente = null;

            if (orden != null) {
                vehiculo = vehiculoDAO.findById(orden.getIdVehiculo());
                if (vehiculo != null) {
                    cliente = clienteDAO.findById(vehiculo.getIdCliente());
                }
            }

            // ========== ENCABEZADO ==========
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph titulo = new Paragraph("FACTURA #" + factura.getIdFactura(), tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("\n"));

            // ========== INFORMACIÓN DEL TALLER ==========
            Font negrita = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12);

            Paragraph taller = new Paragraph("MOTORPLUS TALLER AUTOMOTRIZ", negrita);
            taller.setAlignment(Element.ALIGN_CENTER);
            document.add(taller);

            Paragraph direccion = new Paragraph("Avenida 19 #10N - Cel: (+57)321-980-68-68", normal);
            direccion.setAlignment(Element.ALIGN_CENTER);
            document.add(direccion);

            document.add(new Paragraph("\n"));

            // ========== INFORMACIÓN DEL CLIENTE Y VEHÍCULO ==========
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);

            // Columna izquierda - Información del cliente
            String clienteInfo = "CLIENTE:\n";
            if (cliente != null) {
                clienteInfo += cliente.getNombres() + " " + cliente.getApellidos() + "\n";
                clienteInfo += "Documento: " + cliente.getDocumento() + "\n";
                if (cliente.getTelefono() != null) {
                    clienteInfo += "Tel: " + cliente.getTelefono();
                }
            } else {
                clienteInfo += "No disponible";
            }

            // Columna derecha - Información del vehículo y orden
            String vehiculoInfo = "VEHÍCULO:\n";
            if (vehiculo != null) {
                vehiculoInfo += vehiculo.getMarca() + " " + vehiculo.getModelo() + "\n";
                vehiculoInfo += "Placa: " + vehiculo.getPlaca() + "\n";
                vehiculoInfo += "Color: " + (vehiculo.getColor() != null ? vehiculo.getColor() : "N/A");
            } else {
                vehiculoInfo += "No disponible";
            }

            String ordenInfo = "\nORDEN DE TRABAJO:\n";
            ordenInfo += "N°: " + factura.getIdOrdenTrabajo() + "\n";
            if (orden != null && orden.getDiagnosticoInicial() != null) {
                ordenInfo += "Diagnóstico: " + orden.getDiagnosticoInicial();
            }

            PdfPCell clienteCell = new PdfPCell(new Phrase(clienteInfo, normal));
            clienteCell.setBorder(Rectangle.NO_BORDER);
            clienteCell.setPadding(5);

            PdfPCell vehiculoCell = new PdfPCell(new Phrase(vehiculoInfo + ordenInfo, normal));
            vehiculoCell.setBorder(Rectangle.NO_BORDER);
            vehiculoCell.setPadding(5);

            infoTable.addCell(clienteCell);
            infoTable.addCell(vehiculoCell);
            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // ========== DETALLES DE LA FACTURA ==========
            Paragraph detallesTitulo = new Paragraph("DETALLES DE LA FACTURA", negrita);
            detallesTitulo.setSpacingBefore(10f);
            document.add(detallesTitulo);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaEmision = factura.getFechaEmision().format(formatter);

            PdfPTable detallesTable = new PdfPTable(2);
            detallesTable.setWidthPercentage(100);
            detallesTable.setSpacingBefore(5f);

            agregarFilaTabla(detallesTable, "Fecha de emisión:", fechaEmision, negrita, normal);
            agregarFilaTabla(detallesTable, "Estado del pago:", factura.getEstadoPago(), negrita, normal);

            document.add(detallesTable);

            document.add(new Paragraph("\n"));

            // ========== DESGLOSE DE COSTOS ==========
            Paragraph costosTitulo = new Paragraph("DESGLOSE DE COSTOS", negrita);
            costosTitulo.setSpacingBefore(10f);
            document.add(costosTitulo);

            PdfPTable tablaCostos = new PdfPTable(2);
            tablaCostos.setWidthPercentage(100);
            tablaCostos.setSpacingBefore(5f);

            DecimalFormat df = new DecimalFormat("$#,##0.00");

            // Encabezados de la tabla
            PdfPCell conceptoHeader = new PdfPCell(new Phrase("CONCEPTO", negrita));
            conceptoHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            conceptoHeader.setPadding(8);

            PdfPCell valorHeader = new PdfPCell(new Phrase("VALOR", negrita));
            valorHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            valorHeader.setPadding(8);

            tablaCostos.addCell(conceptoHeader);
            tablaCostos.addCell(valorHeader);

            // Filas de la tabla
            agregarFilaTablaCostos(tablaCostos, "Mano de obra", factura.getCostoManoObra(), df);
            agregarFilaTablaCostos(tablaCostos, "Repuestos y materiales", factura.getCostoRepuestos(), df);
            agregarFilaTablaCostos(tablaCostos, "Impuestos (19%)", factura.getImpuestos(), df);

            // Fila del total
            PdfPCell totalConcepto = new PdfPCell(new Phrase("TOTAL", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            totalConcepto.setPadding(8);
            totalConcepto.setBackgroundColor(BaseColor.LIGHT_GRAY);

            PdfPCell totalValor = new PdfPCell(new Phrase(df.format(factura.getValorTotal()),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            totalValor.setPadding(8);
            totalValor.setBackgroundColor(BaseColor.LIGHT_GRAY);

            tablaCostos.addCell(totalConcepto);
            tablaCostos.addCell(totalValor);

            document.add(tablaCostos);

            // ========== PIE DE PÁGINA ==========
            document.add(new Paragraph("\n\n"));

            Paragraph gracias = new Paragraph("¡Gracias por confiar en MotorPlus!",
                    new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC));
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);

            Paragraph contacto = new Paragraph("Para cualquier consulta, contáctenos al (+57) 321-980-68-68", normal);
            contacto.setAlignment(Element.ALIGN_CENTER);
            document.add(contacto);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    private void agregarFilaTabla(PdfPTable tabla, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);

        tabla.addCell(labelCell);
        tabla.addCell(valueCell);
    }

    private void agregarFilaTablaCostos(PdfPTable tabla, String concepto, BigDecimal valor, DecimalFormat df) {
        PdfPCell conceptoCell = new PdfPCell(new Phrase(concepto));
        conceptoCell.setPadding(6);

        PdfPCell valorCell = new PdfPCell(new Phrase(df.format(valor)));
        valorCell.setPadding(6);

        tabla.addCell(conceptoCell);
        tabla.addCell(valorCell);
    }
}