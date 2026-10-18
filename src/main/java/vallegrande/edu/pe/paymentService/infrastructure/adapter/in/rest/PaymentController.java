package vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.domain.model.Payment;
import vallegrande.edu.pe.paymentService.domain.port.in.CompletePaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.in.CreatePaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.in.QueryPaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.in.UpdatePaymentUseCase;
import vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest.dto.CompletePaymentRequest;
import vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest.dto.CreatePaymentRequest;
import vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest.dto.ReasonRequest;
import vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest.dto.UpdatePaymentRequest;

/**
 * Adaptador de Entrada REST (Driving Adapter).
 *
 * Ubicación: Capa de INFRAESTRUCTURA.
 * 
 * Regla de Arquitectura Hexagonal:
 * - NO contiene lógica de negocio.
 * - Su única función es recibir peticiones HTTP no bloqueantes (WebFlux),
 *   mapear DTOs al modelo de dominio puro (Payment), delegar la ejecución
 *   a los puertos de entrada (Use Cases) y devolver respuestas reactivas.
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final QueryPaymentUseCase queryPaymentUseCase;
    private final UpdatePaymentUseCase updatePaymentUseCase;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. ENDPOINT: Centralizador - Registrar pago inicial (Otros microservicios)
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Payment>> createPayment(@RequestBody CreatePaymentRequest request) {
        Payment domainPayment = mapToDomain(request);
        return createPaymentUseCase.createPayment(domainPayment)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved))
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. ENDPOINT: Frontend de Finanzas - Completar pago (Asignar método y estado)
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> completePayment(
            @PathVariable Long id,
            @RequestBody CompletePaymentRequest request) {
        return completePaymentUseCase.completePayment(
                        id,
                        request.getMetodoPago(),
                        request.getReference(),
                        request.getConfirmedBy())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class,
                        e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. ENDPOINTS: Consultas reactivas
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> findById(@PathVariable Long id) {
        return queryPaymentUseCase.findById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findAll(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return queryPaymentUseCase.findByStatus(status);
        }
        return queryPaymentUseCase.findAll();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findByStatus(@PathVariable String status) {
        return queryPaymentUseCase.findByStatus(status);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findCompletedPayments() {
        return queryPaymentUseCase.findByStatus("COMPLETED");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findPendingPayments() {
        return queryPaymentUseCase.findByStatus("PENDING");
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findCancelledPayments() {
        return queryPaymentUseCase.findByStatus("CANCELLED");
    }

    @GetMapping("/persona/{personaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findByPersonaId(@PathVariable Long personaId) {
        return queryPaymentUseCase.findByPeopleId(personaId);
    }

    @GetMapping("/people/{peopleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findByPeopleId(@PathVariable Long peopleId) {
        return queryPaymentUseCase.findByPeopleId(peopleId);
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Flux<Payment> findByTenantId(@PathVariable Long tenantId) {
        return queryPaymentUseCase.findByTenantId(tenantId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. ENDPOINTS: Flujos de ciclo de vida adicionales
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> cancelPayment(
            @PathVariable Long id,
            @RequestBody ReasonRequest request) {
        return completePaymentUseCase.cancelPayment(id, request.getReason())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> rejectPayment(
            @PathVariable Long id,
            @RequestBody ReasonRequest request) {
        return completePaymentUseCase.rejectPayment(id, request.getReason())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> refundPayment(
            @PathVariable Long id,
            @RequestBody ReasonRequest request) {
        return completePaymentUseCase.refundPayment(id, request.getReason())
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. ENDPOINT: Editar pago existente (solo datos enviados en el formulario y estado)
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> updatePayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request) {
        Payment domainUpdates = mapUpdateToDomain(request);
        return updatePaymentUseCase.updatePayment(id, domainUpdates)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, e -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(IllegalStateException.class, e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public Mono<ResponseEntity<Payment>> patchPayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request) {
        return updatePayment(id, request);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapeo: DTO HTTP → Modelo de Dominio puro
    // ─────────────────────────────────────────────────────────────────────────

    private Payment mapToDomain(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setTenantId(request.getTenantId());
        payment.setPersonaId(request.getPersonaId() != null ? request.getPersonaId() : request.getPeopleId());
        payment.setRequestReferenceId(request.getRequestReferenceId() != null ? request.getRequestReferenceId() : request.getRequestId());
        payment.setMonto(request.getMonto() != null ? request.getMonto() : request.getAmount());
        payment.setItems(request.getItems());
        return payment;
    }

    private Payment mapUpdateToDomain(UpdatePaymentRequest request) {
        if (request == null) return null;
        Payment payment = new Payment();
        payment.setMonto(request.getMonto() != null ? request.getMonto() : request.getAmount());
        payment.setPersonaId(request.getPersonaId() != null ? request.getPersonaId() : request.getPeopleId());
        payment.setRequestReferenceId(request.getRequestReferenceId() != null ? request.getRequestReferenceId() : request.getRequestId());
        payment.setMetodoPago(request.getMetodoPago() != null ? request.getMetodoPago() : request.getPaymentMethod());
        payment.setReference(request.getReference());
        payment.setTenantId(request.getTenantId());
        payment.setCancelReason(request.getCancelReason());
        payment.setConfirmedBy(request.getConfirmedBy());
        payment.setItems(request.getItems());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            payment.setStatus(vallegrande.edu.pe.paymentService.domain.vo.PaymentStatus.fromString(request.getStatus()));
        }
        return payment;
    }
}
