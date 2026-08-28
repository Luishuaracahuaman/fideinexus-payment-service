package vallegrande.edu.pe.paymentService.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.dto.CancelRequestDto;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.service.PaymentService;
// 1. Asegúrate de tener estos imports arriba del todo en PaymentRest.java
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import vallegrande.edu.pe.paymentService.service.PaymentReportService; // Importa tu nuevo servicio

// 2. Modifica la inyección de dependencias (agrégalo debajo de paymentService)
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentRest {

    private final PaymentService paymentService;
    private final PaymentReportService paymentReportService; // <-- AQUÍ SE INYECTA EL SERVICIO DE REPORTES

    // ... (Aquí van tus otros endpoints que ya tenías: findAll, findById, save,
    // etc.) ...

    // ==========================================
    // 3. PEGA ESTOS ENDPOINTS AL FINAL (ANTES DE LA ÚLTIMA LLAVE '}')
    // ==========================================

    @GetMapping(value = "/reportes/sacramentos/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ByteArrayResource>> exportSacramentosPdf() {
        return paymentReportService.generateSacramentosPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_sacramentos.pdf")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = "/reportes/sacramentos/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ByteArrayResource>> exportSacramentosExcel() {
        return paymentReportService.generateSacramentosExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_sacramentos.xlsx")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = "/reportes/libros/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ByteArrayResource>> exportBooksPdf() {
        return paymentReportService.generateBooksPdf()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_libros.pdf")
                        .body(new ByteArrayResource(bytes)));
    }

    @GetMapping(value = "/reportes/libros/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<ByteArrayResource>> exportBooksExcel() {
        return paymentReportService.generateBooksExcel()
                .map(bytes -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_libros.xlsx")
                        .body(new ByteArrayResource(bytes)));
    }
}