package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.CompletePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.CreatePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.QueryPaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.UpdatePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.CompletePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.CreatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.ReasonRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.UpdatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.mapper.PaymentRestMapper;
import pe.edu.vallegrande.fideinexus.payment.dto.PeopleInfo;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.enrichment.PaymentEnrichmentService;

/**
 * Adaptador de Entrada REST (Driving Adapter).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/controller/).
 * 
 * Regla de Arquitectura Hexagonal:
 * - NO contiene lógica de negocio ni mapeos directos.
 * - Su única función es recibir peticiones HTTP no bloqueantes (WebFlux),
 *   delegar la conversión a PaymentRestMapper, invocar los casos de uso
 *   (puertos de entrada) y devolver respuestas reactivas.
 */
@RestController
@RequestMapping({"/api/v1/finance", "/payments", "/finance"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final QueryPaymentUseCase queryPaymentUseCase;
    private final UpdatePaymentUseCase updatePaymentUseCase;
    private final PaymentRestMapper paymentRestMapper;
    private final PaymentEnrichmentService paymentEnrichmentService;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. ENDPOINT: Centralizador - Registrar pago inicial (Otros microservicios)
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Payment>> createPayment(@RequestBody CreatePaymentRequest request) {
        Payment domainPayment = paymentRestMapper.toDomain(request);
        return createPaymentUseCase.createPayment(domainPayment)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. ENDPOINT: Frontend de Finanzas - Completar pago (Asignar método y estado)
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping({"/{id}/complete", "/{id}/confirm"})
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> completePayment(
            @PathVariable Long id,
            @RequestBody(required = false) CompletePaymentRequest request) {
        CompletePaymentRequest req = request != null ? request : new CompletePaymentRequest();
        return completePaymentUseCase.completePayment(
                        id,
                        req.getMetodoPago(),
                        req.getReference(),
                        req.getConfirmedBy())
                .map(ResponseEntity::ok);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. ENDPOINTS: Consultas reactivas
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> findById(@PathVariable Long id) {
        return queryPaymentUseCase.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findAll(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return queryPaymentUseCase.findByStatus(status);
        }
        return queryPaymentUseCase.findAll();
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findByStatus(@PathVariable String status) {
        return queryPaymentUseCase.findByStatus(status);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findCompletedPayments() {
        return queryPaymentUseCase.findByStatus("COMPLETED");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findPendingPayments() {
        return queryPaymentUseCase.findByStatus("PENDING");
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findCancelledPayments() {
        return queryPaymentUseCase.findByStatus("CANCELLED");
    }

    @GetMapping("/persona/{personaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findByPersonaId(@PathVariable Long personaId) {
        return queryPaymentUseCase.findByPeopleId(personaId);
    }

    @GetMapping("/people/{peopleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findByPeopleId(@PathVariable Long peopleId) {
        return queryPaymentUseCase.findByPeopleId(peopleId);
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<Payment> findByTenantId(@PathVariable Long tenantId) {
        return queryPaymentUseCase.findByTenantId(tenantId).switchIfEmpty(queryPaymentUseCase.findAll());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. ENDPOINTS: Flujos de ciclo de vida adicionales
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> cancelPayment(
            @PathVariable Long id,
            @RequestBody ReasonRequest request) {
        return completePaymentUseCase.cancelPayment(id, request.getReason())
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> rejectPayment(
            @PathVariable Long id,
            @RequestBody ReasonRequest request) {
        return completePaymentUseCase.rejectPayment(id, request.getReason())
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> refundPayment(
            @PathVariable Long id,
            @RequestBody ReasonRequest request) {
        return completePaymentUseCase.refundPayment(id, request.getReason())
                .map(ResponseEntity::ok);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. ENDPOINT: Editar pago existente (solo datos enviados en el formulario y estado)
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> updatePayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request) {
        Payment domainUpdates = paymentRestMapper.toDomain(request);
        return updatePaymentUseCase.updatePayment(id, domainUpdates)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<Payment>> patchPayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request) {
        return updatePayment(id, request);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 6. ENDPOINT: Lista de feligreses para el formulario de pago
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping({"/feligreses", "/people", "/personas"})
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PeopleInfo> getFeligreses(
            @RequestParam(required = false) Long tenantId,
            @RequestHeader(value = "X-Tenant-Id", required = false) Long headerTenantId) {
        Long effectiveTenantId = tenantId != null ? tenantId : headerTenantId;
        return paymentEnrichmentService.getFeligreses(effectiveTenantId);
    }
}
