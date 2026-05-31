package vallegrande.edu.pe.paymentservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentservice.model.Payment;
import vallegrande.edu.pe.paymentservice.model.PaymentMethod;
import vallegrande.edu.pe.paymentservice.model.Reason;

public interface PaymentService {

    // ── CRUD de Pagos ────────────────────────────────────────────────────
    Flux<Payment> findAll();
    Flux<Payment> findByTenantId(Long tenantId);
    Flux<Payment> findByTenantIdAndEstado(Long tenantId, String estado);
    Mono<Payment> findById(Long id);
    Mono<Payment> save(Payment payment);
    Mono<Payment> update(Long id, Payment payment);
    Mono<Payment> changeEstado(Long id, String estado);
    Mono<Void>    delete(Long id);

    // ── Catálogos ────────────────────────────────────────────────────────
    Flux<Reason>        findAllReasons();
    Flux<PaymentMethod> findAllPaymentMethods();
}
