package vallegrande.edu.pe.paymentService.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import vallegrande.edu.pe.paymentService.model.Payment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PaymentReportService {

    private final PaymentService paymentService;

    // ====================================================================
    // REPORTES DE SACRAMENTOS (Registros Parroquiales)
    // ====================================================================

    public Mono<byte[]> generateSacramentosPdf() {
        return paymentService.findAll()
                .filter(p -> p.getRequestId() != null) // Solo pagos con solicitud
                .collectList()
                .flatMap(payments -> Mono.fromCallable(() -> {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Document document = new Document(PageSize.A4);
                    PdfWriter.getInstance(document, out);
                    document.open();

                    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(31, 78, 120));
                    Paragraph title = new Paragraph("REPORTE DE REGISTROS Y SACRAMENTOS", titleFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingAfter(20);
                    document.add(title);

                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[] { 1.5f, 3.5f, 2.5f, 2f, 2f, 1.5f });

                    String[] headers = { "Código", "Nombre Completo", "Sacramento", "Fecha", "Estado", "Monto (S/)" };
                    Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                        cell.setBackgroundColor(new Color(31, 78, 120));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(8);
                        table.addCell(cell);
                    }

                    Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                    double total = 0.0;

                    for (Payment p : payments) {
                        table.addCell(new PdfPCell(new Phrase("PAG-" + p.getId(), bodyFont)));
                        String persona = (p.getPeople() != null)
                                ? p.getPeople().getNombre() + " " + p.getPeople().getApellido()
                                : "N/A";
                        table.addCell(new PdfPCell(new Phrase(persona, bodyFont)));
                        String tipo = (p.getRequest() != null && p.getRequest().getType() != null)
                                ? p.getRequest().getType()
                                : "Sacramento";
                        table.addCell(new PdfPCell(new Phrase(tipo, bodyFont)));
                        table.addCell(new PdfPCell(new Phrase(
                                p.getPaymentDate() != null ? p.getPaymentDate().toLocalDate().toString() : "",
                                bodyFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getStatus(), bodyFont)));
                        table.addCell(new PdfPCell(
                                new Phrase(p.getAmount() != null ? p.getAmount().toString() : "0.00", bodyFont)));

                        if ("CONFIRMADO".equals(p.getStatus()) && p.getAmount() != null) {
                            total += p.getAmount().doubleValue();
                        }
                    }
                    document.add(table);

                    document.add(Chunk.NEWLINE);
                    Paragraph summary = new Paragraph("Total Recaudado (S/.): " + String.format("%.2f", total),
                            titleFont);
                    summary.setAlignment(Element.ALIGN_RIGHT);
                    document.add(summary);

                    document.close();
                    return out.toByteArray();
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<byte[]> generateSacramentosExcel() {
        return paymentService.findAll()
                .filter(p -> p.getRequestId() != null)
                .collectList()
                .flatMap(payments -> Mono.fromCallable(() -> {
                    try (Workbook workbook = new XSSFWorkbook();
                            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        Sheet sheet = workbook.createSheet("Sacramentos");

                        CellStyle headerStyle = workbook.createCellStyle();
                        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                        font.setColor(IndexedColors.WHITE.getIndex());
                        font.setBold(true);
                        headerStyle.setFont(font);

                        Row headerRow = sheet.createRow(0);
                        String[] columns = { "Código", "Nombre Completo", "Sacramento", "Fecha", "Estado",
                                "Monto (S/)" };
                        for (int i = 0; i < columns.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellValue(columns[i]);
                            cell.setCellStyle(headerStyle);
                        }

                        int rowIdx = 1;
                        for (Payment p : payments) {
                            Row row = sheet.createRow(rowIdx++);
                            row.createCell(0).setCellValue("PAG-" + p.getId());
                            String persona = (p.getPeople() != null)
                                    ? p.getPeople().getNombre() + " " + p.getPeople().getApellido()
                                    : "N/A";
                            row.createCell(1).setCellValue(persona);
                            String tipo = (p.getRequest() != null && p.getRequest().getType() != null)
                                    ? p.getRequest().getType()
                                    : "Sacramento";
                            row.createCell(2).setCellValue(tipo);
                            row.createCell(3).setCellValue(
                                    p.getPaymentDate() != null ? p.getPaymentDate().toLocalDate().toString() : "");
                            row.createCell(4).setCellValue(p.getStatus());
                            if (p.getAmount() != null)
                                row.createCell(5).setCellValue(p.getAmount().doubleValue());
                        }

                        for (int i = 0; i < columns.length; i++)
                            sheet.autoSizeColumn(i);
                        workbook.write(out);
                        return out.toByteArray();
                    }
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    // ====================================================================
    // REPORTES DE LIBROS (Venta de libros)
    // ====================================================================

    public Mono<byte[]> generateBooksPdf() {
        return paymentService.findAll()
                .filter(p -> p.getItems() != null && !p.getItems().isEmpty()) // Solo pagos con libros
                .collectList()
                .flatMap(payments -> Mono.fromCallable(() -> {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Document document = new Document(PageSize.A4);
                    PdfWriter.getInstance(document, out);
                    document.open();

                    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new Color(31, 78, 120));
                    Paragraph title = new Paragraph("REPORTE DE VENTA DE LIBROS", titleFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    title.setSpacingAfter(20);
                    document.add(title);

                    PdfPTable table = new PdfPTable(6);
                    table.setWidthPercentage(100);
                    table.setWidths(new float[] { 1.5f, 3.5f, 2.5f, 2f, 2f, 1.5f });

                    String[] headers = { "Código", "Comprador", "Cant. Libros", "Fecha", "Estado", "Monto (S/)" };
                    Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
                    for (String header : headers) {
                        PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                        cell.setBackgroundColor(new Color(31, 78, 120));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPadding(8);
                        table.addCell(cell);
                    }

                    Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                    double total = 0.0;

                    for (Payment p : payments) {
                        table.addCell(new PdfPCell(new Phrase("PAG-" + p.getId(), bodyFont)));
                        String persona = (p.getPeople() != null)
                                ? p.getPeople().getNombre() + " " + p.getPeople().getApellido()
                                : "N/A";
                        table.addCell(new PdfPCell(new Phrase(persona, bodyFont)));

                        int cantLibros = p.getItems().stream().mapToInt(Payment.BookItem::getQuantity).sum();
                        table.addCell(new PdfPCell(new Phrase(String.valueOf(cantLibros), bodyFont)));

                        table.addCell(new PdfPCell(new Phrase(
                                p.getPaymentDate() != null ? p.getPaymentDate().toLocalDate().toString() : "",
                                bodyFont)));
                        table.addCell(new PdfPCell(new Phrase(p.getStatus(), bodyFont)));
                        table.addCell(new PdfPCell(
                                new Phrase(p.getAmount() != null ? p.getAmount().toString() : "0.00", bodyFont)));

                        if ("CONFIRMADO".equals(p.getStatus()) && p.getAmount() != null) {
                            total += p.getAmount().doubleValue();
                        }
                    }
                    document.add(table);

                    document.add(Chunk.NEWLINE);
                    Paragraph summary = new Paragraph("Total Recaudado (S/.): " + String.format("%.2f", total),
                            titleFont);
                    summary.setAlignment(Element.ALIGN_RIGHT);
                    document.add(summary);

                    document.close();
                    return out.toByteArray();
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    public Mono<byte[]> generateBooksExcel() {
        return paymentService.findAll()
                .filter(p -> p.getItems() != null && !p.getItems().isEmpty())
                .collectList()
                .flatMap(payments -> Mono.fromCallable(() -> {
                    try (Workbook workbook = new XSSFWorkbook();
                            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        Sheet sheet = workbook.createSheet("Libros");

                        CellStyle headerStyle = workbook.createCellStyle();
                        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                        font.setColor(IndexedColors.WHITE.getIndex());
                        font.setBold(true);
                        headerStyle.setFont(font);

                        Row headerRow = sheet.createRow(0);
                        String[] columns = { "Código", "Comprador", "Cant. Libros", "Fecha", "Estado", "Monto (S/)" };
                        for (int i = 0; i < columns.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellValue(columns[i]);
                            cell.setCellStyle(headerStyle);
                        }

                        int rowIdx = 1;
                        for (Payment p : payments) {
                            Row row = sheet.createRow(rowIdx++);
                            row.createCell(0).setCellValue("PAG-" + p.getId());
                            String persona = (p.getPeople() != null)
                                    ? p.getPeople().getNombre() + " " + p.getPeople().getApellido()
                                    : "N/A";
                            row.createCell(1).setCellValue(persona);
                            int cantLibros = p.getItems().stream().mapToInt(Payment.BookItem::getQuantity).sum();
                            row.createCell(2).setCellValue(cantLibros);
                            row.createCell(3).setCellValue(
                                    p.getPaymentDate() != null ? p.getPaymentDate().toLocalDate().toString() : "");
                            row.createCell(4).setCellValue(p.getStatus());
                            if (p.getAmount() != null)
                                row.createCell(5).setCellValue(p.getAmount().doubleValue());
                        }

                        for (int i = 0; i < columns.length; i++)
                            sheet.autoSizeColumn(i);
                        workbook.write(out);
                        return out.toByteArray();
                    }
                }).subscribeOn(Schedulers.boundedElastic()));
    }
}