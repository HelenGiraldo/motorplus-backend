package com.motorplus.backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportePdfService {

    public byte[] generarPDF(String titulo, List<Map<String, Object>> datos) {
        if (datos == null || datos.isEmpty()) {
            return generarPDFVacio(titulo);
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate()); // Horizontal para más espacio
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Agregar header y footer personalizados
            writer.setPageEvent(new PdfPageEvent());

            document.open();

            // Título
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph header = new Paragraph(titulo, tituloFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(15);
            document.add(header);

            // Fecha de generación
            Font fechaFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Paragraph fecha = new Paragraph("Generado el: " + java.time.LocalDate.now(), fechaFont);
            fecha.setAlignment(Element.ALIGN_CENTER);
            fecha.setSpacingAfter(20);
            document.add(fecha);

            // Tabla con mejoras
            Map<String, Object> firstRow = datos.get(0);
            PdfPTable table = new PdfPTable(firstRow.size());
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Configurar anchos de columnas
            float[] columnWidths = new float[firstRow.size()];
            for (int i = 0; i < columnWidths.length; i++) {
                columnWidths[i] = 100f / firstRow.size();
            }
            table.setWidths(columnWidths);

            // Encabezados mejorados
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            for (String column : firstRow.keySet()) {
                PdfPCell cell = new PdfPCell(new Phrase(column.toUpperCase(), headerFont));
                cell.setBackgroundColor(new BaseColor(51, 122, 183)); // Azul más profesional
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            // Filas con mejor formato
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
            int rowCount = 0;

            for (Map<String, Object> fila : datos) {
                for (Object valor : fila.values()) {
                    String texto = valor != null ? valor.toString() : "—";

                    // Truncar texto muy largo para evitar desbordamiento
                    if (texto.length() > 50) {
                        texto = texto.substring(0, 47) + "...";
                    }

                    PdfPCell cell = new PdfPCell(new Phrase(texto, cellFont));
                    cell.setPadding(6);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    // Color alternado para filas
                    if (rowCount % 2 == 0) {
                        cell.setBackgroundColor(new BaseColor(248, 249, 250));
                    }

                    table.addCell(cell);
                }
                rowCount++;
            }

            document.add(table);

            // Agregar resumen
            Paragraph resumen = new Paragraph(
                    "Total de registros: " + datos.size(),
                    new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)
            );
            resumen.setSpacingBefore(10);
            document.add(resumen);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return generarPDFError(titulo, e.getMessage());
        }
    }

    private byte[] generarPDFVacio(String titulo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph p = new Paragraph(titulo + "\n\nNO HAY DATOS DISPONIBLES PARA ESTE REPORTE", font);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(20);
            document.add(p);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

    private byte[] generarPDFError(String titulo, String error) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font errorFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.RED);

            Paragraph p1 = new Paragraph(titulo, tituloFont);
            p1.setAlignment(Element.ALIGN_CENTER);
            p1.setSpacingAfter(10);
            document.add(p1);

            Paragraph p2 = new Paragraph("Error al generar el reporte: " + error, errorFont);
            p2.setAlignment(Element.ALIGN_CENTER);
            document.add(p2);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

    // Clase interna para header y footer
    class PdfPageEvent extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                // Footer
                PdfPTable footer = new PdfPTable(1);
                footer.setTotalWidth(527);
                footer.setLockedWidth(true);
                footer.getDefaultCell().setFixedHeight(30);
                footer.getDefaultCell().setBorder(Rectangle.TOP);
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
                footer.addCell(new Phrase("Página " + writer.getPageNumber(), footerFont));

                footer.writeSelectedRows(0, -1, 34, 50, writer.getDirectContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}