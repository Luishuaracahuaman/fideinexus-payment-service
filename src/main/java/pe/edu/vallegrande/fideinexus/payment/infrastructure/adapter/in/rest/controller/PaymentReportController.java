package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.application.usecase.PaymentReportService;

/**
 * Adaptador de ENTRADA: Controlador REST para reportes de pagos.
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/controller/).
 * Convenciones:
 * - Rutas en plural y kebab-case (/api/v1/payments/reports).
 * - Generación reactiva de reportes en PDF y Excel.
 */
@RestController
@RequestMapping({"/api/v1/payments/reports", "/api/v1/finance/reportes", "/payments/reportes", "/finance/reportes"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentReportController {

    private final PaymentReportService paymentReportService;

    @GetMapping(value = {"/sacraments/pdf", "/sacramentos/pdf"}, produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportSacramentosPdf() {
        return paymentReportService.generateSacramentosPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_sacramentos.pdf")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = {"/sacraments/excel", "/sacramentos/excel"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportSacramentosExcel() {
        return paymentReportService.generateSacramentosExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_sacramentos.xlsx")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = {"/books/pdf", "/libros/pdf"}, produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportBooksPdf() {
        return paymentReportService.generateBooksPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_libros.pdf")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = {"/books/excel", "/libros/excel"}, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportBooksExcel() {
        return paymentReportService.generateBooksExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_libros.xlsx")
                        .body(new ByteArrayResource(bytes)));
    }
}
