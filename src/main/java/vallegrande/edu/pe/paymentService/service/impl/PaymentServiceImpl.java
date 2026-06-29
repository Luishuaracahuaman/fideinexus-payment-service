package vallegrande.edu.pe.paymentService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.model.PaymentMethod;
import vallegrande.edu.pe.paymentService.model.Reason;
import vallegrande.edu.pe.paymentService.repository.PaymentMethodRepository;
import vallegrande.edu.pe.paymentService.repository.PaymentRepository;
import vallegrande.edu.pe.paymentService.repository.ReasonRepository;
import vallegrande.edu.pe.paymentService.service.PaymentService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository        paymentRepository;
    private final ReasonRepository         reasonRepository;
    private final PaymentMethodRepository  paymentMethodRepository;

    // ── Enriquecimiento: agrega datos de reason y paymentMethod ──────────
    private Mono<Payment> enrich(Payment p) {
        Mono<Payment> enriched = Mono.just(p);

        if (p.getReasonId() != null) {
            enriched = enriched.flatMap(pay ->
                reasonRepository.findById(pay.getReasonId())
                    .doOnNext(pay::setReason)
                    .thenReturn(pay)
            );
        }

        if (p.getPaymentMethodId() != null) {
            enriched = enriched.flatMap(pay ->
                paymentMethodRepository.findById(pay.getPaymentMethodId())
                    .doOnNext(pay::setPaymentMethod)
                    .thenReturn(pay)
            );
        }

        return enriched;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Flux<Payment> findAll() {
        return paymentRepository.findAll().flatMap(this::enrich);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Payment> findByTenantId(Long tenantId) {
        return paymentRepository.findByTenantId(tenantId).flatMap(this::enrich);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Payment> findByTenantIdAndEstado(Long tenantId, String estado) {
        return paymentRepository.findByTenantIdAndEstado(tenantId, estado).flatMap(this::enrich);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Payment> findById(Long id) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pago no encontrado con id: " + id)))
                .flatMap(this::enrich);
    }

    @Override
    @Transactional
    public Mono<Payment> save(Payment payment) {
        payment.setCreatedAt(LocalDateTime.now());
        if (payment.getEstado() == null || payment.getEstado().isBlank()) {
            payment.setEstado("P");
        }
        // Extraer IDs de los objetos anidados (si vienen del frontend como objetos)
        if (payment.getReason() != null && payment.getReasonId() == null) {
            payment.setReasonId(payment.getReason().getId());
        }
        if (payment.getPaymentMethod() != null && payment.getPaymentMethodId() == null) {
            payment.setPaymentMethodId(payment.getPaymentMethod().getId());
        }
        return paymentRepository.save(payment).flatMap(this::enrich);
    }

    @Override
    @Transactional
    public Mono<Payment> update(Long id, Payment payment) {
        return findById(id).flatMap(existing -> {
            existing.setTenantId(payment.getTenantId());
            existing.setPeopleId(payment.getPeopleId());
            existing.setMonto(payment.getMonto());
            existing.setFechaPago(payment.getFechaPago());
            existing.setEstado(payment.getEstado());
            existing.setReferencia(payment.getReferencia());
            if (payment.getReasonId() != null)        existing.setReasonId(payment.getReasonId());
            if (payment.getPaymentMethodId() != null) existing.setPaymentMethodId(payment.getPaymentMethodId());
            return paymentRepository.save(existing).flatMap(this::enrich);
        });
    }

    @Override
    @Transactional
    public Mono<Payment> changeEstado(Long id, String estado) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pago no encontrado con id: " + id)))
                .flatMap(existing -> {
                    existing.setEstado(estado);
                    return paymentRepository.save(existing).flatMap(this::enrich);
                });
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pago no encontrado con id: " + id)))
                .flatMap(paymentRepository::delete);
    }

    // ── Catálogos ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Flux<Reason> findAllReasons() {
        return reasonRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PaymentMethod> findAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }
}
