package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.application.dto.response.PeopleInfo;
import pe.edu.vallegrande.fideinexus.payment.application.dto.request.CompletePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.application.dto.request.CreatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.application.dto.request.ReasonRequest;
import pe.edu.vallegrande.fideinexus.payment.application.dto.request.UpdatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.application.dto.response.PaymentResponse;
import pe.edu.vallegrande.fideinexus.payment.application.dto.response.PaymentEnrichedResponse;
import pe.edu.vallegrande.fideinexus.payment.application.mapper.PaymentRestMapper;
import pe.edu.vallegrande.fideinexus.payment.application.usecase.PaymentEnrichmentService;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.CompletePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.CreatePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.QueryPaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.UpdatePaymentUseCase;

/**
 * Adaptador de Entrada REST (Driving Adapter).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/controller/).
 * Convenciones:
 * - Rutas en plural y kebab-case (/api/v1/payments).
 * - Variables en camelCase estricto.
 * - Validación declarativa con @Valid en todos los payloads de entrada.
 * - Fronteras limpias: Request DTO -> Dominio -> Response DTO.
 */
@RestController
@RequestMapping({"/api/v1/payments", "/payments", "/api/v1/finance/payments", "/finance"})
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
    // 1. ENDPOINT: Centralizador - Registrar pago inicial
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<PaymentResponse>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        Payment domainPayment = paymentRestMapper.toDomain(request);
        return createPaymentUseCase.createPayment(domainPayment)
                .map(paymentRestMapper::toResponse)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. ENDPOINT: Frontend de Finanzas - Completar pago (Asignar método y estado)
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping({"/{id}/complete", "/{id}/confirm"})
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> completePayment(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CompletePaymentRequest request) {
        CompletePaymentRequest req = request != null ? request : new CompletePaymentRequest();
        return completePaymentUseCase.completePayment(
                        id,
                        req.getMetodoPago(),
                        req.getReference(),
                        req.getConfirmedBy())
                .map(paymentRestMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. ENDPOINTS: Consultas reactivas
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/full")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentEnrichedResponse>> getPaymentFull(@PathVariable Long id) {
        return paymentEnrichmentService.getPaymentFull(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> findById(@PathVariable Long id) {
        return queryPaymentUseCase.findById(id)
                .map(paymentRestMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findAll(@RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return queryPaymentUseCase.findByStatus(status)
                    .map(paymentRestMapper::toResponse);
        }
        return queryPaymentUseCase.findAll()
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findByStatus(@PathVariable String status) {
        return queryPaymentUseCase.findByStatus(status)
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findCompletedPayments() {
        return queryPaymentUseCase.findByStatus("COMPLETED")
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findPendingPayments() {
        return queryPaymentUseCase.findByStatus("PENDING")
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findCancelledPayments() {
        return queryPaymentUseCase.findByStatus("CANCELLED")
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/persona/{personaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findByPersonaId(@PathVariable Long personaId) {
        return queryPaymentUseCase.findByPeopleId(personaId)
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/people/{peopleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findByPeopleId(@PathVariable Long peopleId) {
        return queryPaymentUseCase.findByPeopleId(peopleId)
                .map(paymentRestMapper::toResponse);
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Flux<PaymentResponse> findByTenantId(@PathVariable Long tenantId) {
        return queryPaymentUseCase.findByTenantId(tenantId)
                .switchIfEmpty(queryPaymentUseCase.findAll())
                .map(paymentRestMapper::toResponse);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. ENDPOINTS: Flujos de ciclo de vida adicionales
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> cancelPayment(
            @PathVariable Long id,
            @Valid @RequestBody ReasonRequest request) {
        return completePaymentUseCase.cancelPayment(id, request.getReason())
                .map(paymentRestMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> rejectPayment(
            @PathVariable Long id,
            @Valid @RequestBody ReasonRequest request) {
        return completePaymentUseCase.rejectPayment(id, request.getReason())
                .map(paymentRestMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> refundPayment(
            @PathVariable Long id,
            @Valid @RequestBody ReasonRequest request) {
        return completePaymentUseCase.refundPayment(id, request.getReason())
                .map(paymentRestMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. ENDPOINT: Editar pago existente
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentRequest request) {
        Payment domainUpdates = paymentRestMapper.toDomain(request);
        return updatePaymentUseCase.updatePayment(id, domainUpdates)
                .map(paymentRestMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'SUPERADMIN', 'PARROCO', 'SECRETARIO')")
    public Mono<ResponseEntity<PaymentResponse>> patchPayment(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentRequest request) {
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
