package vallegrande.edu.pe.paymentService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.client.BookClient;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.model.PaymentMethod;
import vallegrande.edu.pe.paymentService.model.Reason;
import vallegrande.edu.pe.paymentService.model.People;
import vallegrande.edu.pe.paymentService.repository.PaymentMethodRepository;
import vallegrande.edu.pe.paymentService.repository.PaymentRepository;
import vallegrande.edu.pe.paymentService.repository.PeopleRepository;
import vallegrande.edu.pe.paymentService.repository.ReasonRepository;
import vallegrande.edu.pe.paymentService.service.PaymentService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReasonRepository reasonRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PeopleRepository peopleRepository;
    private final BookClient bookClient;

    // ── Enriquecimiento: agrega datos de reason, paymentMethod, people y book ──────────
    private Mono<Payment> enrich(Payment p) {
        Mono<Payment> enriched = Mono.just(p);

        if (p.getReasonId() != null) {
            enriched = enriched.flatMap(pay ->
                reasonRepository.findById(pay.getReasonId())
                    .map(reason -> {
                        pay.setReason(reason);
                        return pay;
                    })
                    .defaultIfEmpty(pay)
            );
        }

        if (p.getPaymentMethodId() != null) {
            enriched = enriched.flatMap(pay ->
                paymentMethodRepository.findById(pay.getPaymentMethodId())
                    .map(method -> {
                        pay.setPaymentMethod(method);
                        return pay;
                    })
                    .defaultIfEmpty(pay)
            );
        }

        if (p.getPeopleId() != null) {
            enriched = enriched.flatMap(pay ->
                peopleRepository.findById(pay.getPeopleId())
                    .map(people -> {
                        pay.setPeople(people);
                        return pay;
                    })
                    .defaultIfEmpty(pay)
            );
        }

        if (p.getBookId() != null) {
            enriched = enriched.flatMap(pay ->
                bookClient.findById(p.getBookId())
                    .map(book -> {
                        pay.setBook(book);
                        return pay;
                    })
                    .onErrorResume(e -> Mono.just(pay)) // Si falla el cliente HTTP, devolvemos el pago sin libro
                    .defaultIfEmpty(pay)
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
        if (payment.getId() == null || payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }
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

        // Validación de Tenant y People
        return peopleRepository.findById(payment.getPeopleId())
                .switchIfEmpty(Mono.error(new RuntimeException("La persona con id " + payment.getPeopleId() + " no existe.")))
                .flatMap(people -> {
                    if (!people.getTenantId().equals(payment.getTenantId())) {
                        return Mono.error(new RuntimeException("La persona no pertenece al tenant indicado."));
                    }
                    return paymentRepository.save(payment).flatMap(this::enrich);
                });
    }

    @Override
    @Transactional
    public Mono<Payment> update(Long id, Payment payment) {
        return paymentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pago no encontrado con id: " + id)))
                .flatMap(existing -> {
                    // Validar si el peopleId cambió o no
                    Long targetPeopleId = payment.getPeopleId() != null ? payment.getPeopleId() : existing.getPeopleId();
                    Long targetTenantId = payment.getTenantId() != null ? payment.getTenantId() : existing.getTenantId();

                    return peopleRepository.findById(targetPeopleId)
                            .switchIfEmpty(Mono.error(new RuntimeException("La persona con id " + targetPeopleId + " no existe.")))
                            .flatMap(people -> {
                                if (!people.getTenantId().equals(targetTenantId)) {
                                    return Mono.error(new RuntimeException("La persona no pertenece al tenant indicado."));
                                }

                                // Solo sobreescribir si el valor entrante no es null
                                if (payment.getTenantId() != null)      existing.setTenantId(payment.getTenantId());
                                if (payment.getPeopleId() != null)      existing.setPeopleId(payment.getPeopleId());
                                if (payment.getMonto() != null)         existing.setMonto(payment.getMonto());
                                if (payment.getFechaPago() != null)     existing.setFechaPago(payment.getFechaPago());
                                if (payment.getEstado() != null)        existing.setEstado(payment.getEstado());
                                if (payment.getReferencia() != null)    existing.setReferencia(payment.getReferencia());
                                if (payment.getReasonId() != null)      existing.setReasonId(payment.getReasonId());
                                if (payment.getPaymentMethodId() != null) existing.setPaymentMethodId(payment.getPaymentMethodId());
                                if (payment.getBookId() != null)        existing.setBookId(payment.getBookId());

                                return paymentRepository.save(existing).flatMap(this::enrich);
                            });
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
                .flatMap(existing -> paymentRepository.delete(existing).then());
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
