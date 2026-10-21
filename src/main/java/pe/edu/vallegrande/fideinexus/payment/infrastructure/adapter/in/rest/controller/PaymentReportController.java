package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.service.PaymentReportService;

/**
 * Adaptador de ENTRADA: Controlador REST para reportes de pagos.
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/controller/).
 *
 * Expone endpoints para generación de reportes en PDF y Excel.
 * Delega la generación de bytes al servicio de infraestructura PaymentReportService.
 *
 * Endpoints:
 *  GET /payments/reportes/sacramentos/pdf    → Reporte PDF de sacramentos
 *  GET /payments/reportes/sacramentos/excel  → Reporte Excel de sacramentos
 *  GET /payments/reportes/libros/pdf         → Reporte PDF de libros
 *  GET /payments/reportes/libros/excel       → Reporte Excel de libros
 */
@RestController
@RequestMapping({"/api/v1/finance/reportes", "/payments/reportes", "/finance/reportes"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentReportController {

    private final PaymentReportService paymentReportService;

    @GetMapping(value = "/sacramentos/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportSacramentosPdf() {
        return paymentReportService.generateSacramentosPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_sacramentos.pdf")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = "/sacramentos/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportSacramentosExcel() {
        return paymentReportService.generateSacramentosExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_sacramentos.xlsx")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = "/libros/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportBooksPdf() {
        return paymentReportService.generateBooksPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_libros.pdf")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = "/libros/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<ByteArrayResource>> exportBooksExcel() {
        return paymentReportService.generateBooksExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=reporte_libros.xlsx")
                        .body(new ByteArrayResource(bytes)));
    }
}
