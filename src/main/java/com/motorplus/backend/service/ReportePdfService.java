package com.motorplus.backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph header = new Paragraph(titulo, tituloFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);

            // Tabla
            Map<String, Object> firstRow = datos.get(0);
            PdfPTable table = new PdfPTable(firstRow.size());
            table.setWidthPercentage(100);

            // Encabezados
            for (String column : firstRow.keySet()) {
                PdfPCell cell = new PdfPCell(new Phrase(column.toUpperCase()));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Filas
            for (Map<String, Object> fila : datos) {
                for (Object valor : fila.values()) {
                    table.addCell(valor != null ? valor.toString() : "—");
                }
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] generarPDFVacio(String titulo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Paragraph p = new Paragraph(titulo + "\n\nNO HAY DATOS DISPONIBLES.");
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }
}

