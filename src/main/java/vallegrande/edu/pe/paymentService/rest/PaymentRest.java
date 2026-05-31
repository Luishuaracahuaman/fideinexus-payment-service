package vallegrande.edu.pe.paymentservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentservice.model.Payment;
import vallegrande.edu.pe.paymentservice.model.PaymentMethod;
import vallegrande.edu.pe.paymentservice.model.Reason;
import vallegrande.edu.pe.paymentservice.service.PaymentService;

@RestController
@RequestMapping("/finanzas")
@RequiredArgsConstructor
public class PaymentRest {

    private final PaymentService paymentService;

    // ── Listar todos los pagos ───────────────────────────────────────────
    @GetMapping
    public Flux<Payment> findAll() {
        return paymentService.findAll();
    }

    // ── Listar pagos por tenant (con filtro opcional de estado) ──────────
    @GetMapping("/tenant/{tenantId}")
    public Flux<Payment> findByTenantId(
            @PathVariable Long tenantId,
            @RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return paymentService.findByTenantIdAndEstado(tenantId, estado);
        }
        return paymentService.findByTenantId(tenantId);
    }

    // ── Obtener un pago por ID ───────────────────────────────────────────
    @GetMapping("/{id}")
    public Mono<Payment> findById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    // ── Crear un nuevo pago (TRANSACCIÓN) ────────────────────────────────
    @PostMapping("/guardar")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Payment> save(@RequestBody Payment payment) {
        return paymentService.save(payment);
    }

    // ── Actualizar un pago completo (TRANSACCIÓN) ────────────────────────
    @PutMapping("/{id}")
    public Mono<Payment> update(@PathVariable Long id, @RequestBody Payment payment) {
        return paymentService.update(id, payment);
    }

    // ── Cambiar estado de un pago: P -> A (Confirmado) (TRANSACCIÓN) ─────
    @PatchMapping("/{id}/estado")
    public Mono<Payment> changeEstado(@PathVariable Long id, @RequestParam String estado) {
        return paymentService.changeEstado(id, estado);
    }

    // ── Eliminar un pago (TRANSACCIÓN) ───────────────────────────────────
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return paymentService.delete(id);
    }

    // ── Catálogos ────────────────────────────────────────────────────────
    @GetMapping("/razones")
    public Flux<Reason> findAllReasons() {
        return paymentService.findAllReasons();
    }

    @GetMapping("/metodos-pago")
    public Flux<PaymentMethod> findAllPaymentMethods() {
        return paymentService.findAllPaymentMethods();
    }
}
