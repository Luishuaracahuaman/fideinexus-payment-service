package vallegrande.edu.pe.paymentService.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.client.BookClient;
import vallegrande.edu.pe.paymentService.client.PeopleClient;
import vallegrande.edu.pe.paymentService.client.RequestClient;
import vallegrande.edu.pe.paymentService.dto.CancelRequestDto;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.model.Payment.BookItem;
import vallegrande.edu.pe.paymentService.repository.PaymentRepository;
import vallegrande.edu.pe.paymentService.service.PaymentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookClient bookClient;
    private final RequestClient requestClient;
    private final PeopleClient peopleClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Consultas ─────────────────────────────────────────────────────────────

    @Override
    public Flux<Payment> findAll() {
        return paymentRepository.findAll()
                .map(this::deserializeItems)
                .flatMap(this::enrich);
    }

    @Override
    public Mono<Payment> findById(Long id) {
        return paymentRepository.findById(id)
                .map(this::deserializeItems)
                .flatMap(this::enrich);
    }

    @Override
    public Flux<Payment> findByTenant(Long tenantId) {
        return paymentRepository.findByTenantId(tenantId)
                .map(this::deserializeItems)
                .flatMap(this::enrich);
    }

    @Override
    public Flux<Payment> findByPeople(Long peopleId) {
        return paymentRepository.findByPeopleId(peopleId)
                .map(this::deserializeItems)
                .flatMap(this::enrich);
    }

    // ── Creación ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Mono<Payment> create(Payment payment) {
        validateOrigin(payment);
        serializeItems(payment);

        payment.setStatus("POR CONFIRMAR");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment)
                .map(this::deserializeItems)
                .flatMap(this::enrich);
    }

    // ── Actualización ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Mono<Payment> update(Long id, Payment payment) {
        return paymentRepository.findById(id)
                .flatMap(existing -> {
                    if (!"POR CONFIRMAR".equals(existing.getStatus())) {
                        return Mono.error(new IllegalStateException("Solo se pueden actualizar pagos en estado POR CONFIRMAR"));
                    }
                    validateOrigin(payment);

                    existing.setTenantId(payment.getTenantId());
                    existing.setPeopleId(payment.getPeopleId());
                    existing.setRequestId(payment.getRequestId());
                    existing.setAmount(payment.getAmount());
                    existing.setPaymentMethod(payment.getPaymentMethod());
                    existing.setReference(payment.getReference());
                    existing.setItems(payment.getItems());
                    serializeItems(existing);
                    existing.setUpdatedAt(LocalDateTime.now());

                    return paymentRepository.save(existing)
                            .map(this::deserializeItems)
                            .flatMap(this::enrich);
                });
    }

    // ── Transiciones de Estado ────────────────────────────────────────────────

    @Override
    @Transactional
    public Mono<Payment> confirm(Long id, Long confirmedBy) {
        return paymentRepository.findById(id)
                .map(this::deserializeItems)
                .flatMap(payment -> {
                    if (!"POR CONFIRMAR".equals(payment.getStatus())) {
                        return Mono.error(new IllegalStateException("El pago no está en estado POR CONFIRMAR"));
                    }
                    payment.setStatus("CONFIRMADO");
                    payment.setConfirmedBy(confirmedBy);
                    payment.setConfirmedAt(LocalDateTime.now());
                    payment.setUpdatedAt(LocalDateTime.now());

                    Mono<Payment> saveMono = paymentRepository.save(payment);

                    if (payment.getItems() != null && !payment.getItems().isEmpty()) {
                        List<Mono<Void>> decreaseOps = new ArrayList<>();
                        for (BookItem item : payment.getItems()) {
                            decreaseOps.add(
                                bookClient.decreaseStock(item.getBookId(), item.getQuantity())
                                    .doOnSuccess(v -> System.out.println(
                                        "[STOCK] Descontado stock del libro " + item.getBookId()
                                        + ", cantidad: " + item.getQuantity()))
                                    .onErrorResume(e -> {
                                        System.err.println(
                                            "[STOCK ERROR] Fallo al restar stock del libro "
                                            + item.getBookId() + " (qty=" + item.getQuantity() + "): "
                                            + e.getClass().getSimpleName() + " - " + e.getMessage());
                                        return Mono.error(new IllegalStateException("Bajo stock"));
                                    }));
                        }
                        return Flux.concat(decreaseOps).then(saveMono);
                    }

                    return saveMono;
                }).flatMap(this::enrich);
    }

    @Override
    @Transactional
    public Mono<Payment> cancel(Long id, CancelRequestDto dto) {
        return paymentRepository.findById(id)
                .map(this::deserializeItems)
                .flatMap(payment -> {
                    if (!"POR CONFIRMAR".equals(payment.getStatus())) {
                        return Mono.error(new IllegalStateException("El pago no está en estado POR CONFIRMAR"));
                    }
                    payment.setStatus("ANULADO");
                    payment.setCancelReason(dto.getReason());
                    payment.setUpdatedAt(LocalDateTime.now());
                    return paymentRepository.save(payment);
                }).flatMap(this::enrich);
    }

    @Override
    @Transactional
    public Mono<Payment> reject(Long id, CancelRequestDto dto) {
        return paymentRepository.findById(id)
                .map(this::deserializeItems)
                .flatMap(payment -> {
                    if (!"POR CONFIRMAR".equals(payment.getStatus())) {
                        return Mono.error(new IllegalStateException("Solo se pueden rechazar pagos en estado POR CONFIRMAR"));
                    }
                    payment.setStatus("RECHAZADO");
                    payment.setCancelReason(dto.getReason());
                    payment.setUpdatedAt(LocalDateTime.now());
                    return paymentRepository.save(payment);
                }).flatMap(this::enrich);
    }

    @Override
    @Transactional
    public Mono<Payment> refund(Long id, CancelRequestDto dto) {
        return paymentRepository.findById(id)
                .map(this::deserializeItems)
                .flatMap(payment -> {
                    if (!"CONFIRMADO".equals(payment.getStatus())) {
                        return Mono.error(new IllegalStateException("Solo se pueden reembolsar pagos en estado CONFIRMADO"));
                    }
                    payment.setStatus("REEMBOLSADO");
                    payment.setCancelReason(dto.getReason());
                    payment.setUpdatedAt(LocalDateTime.now());

                    Mono<Payment> saveMono = paymentRepository.save(payment);

                    if (payment.getItems() != null && !payment.getItems().isEmpty()) {
                        List<Mono<Void>> increaseOps = new ArrayList<>();
                        for (BookItem item : payment.getItems()) {
                            increaseOps.add(
                                bookClient.increaseStock(item.getBookId(), item.getQuantity())
                                    .doOnSuccess(v -> System.out.println(
                                        "[STOCK] Restaurado stock del libro " + item.getBookId()
                                        + ", cantidad: " + item.getQuantity()))
                                    .onErrorResume(e -> {
                                        System.err.println(
                                            "[STOCK ERROR] Fallo al restaurar stock del libro "
                                            + item.getBookId() + " (qty=" + item.getQuantity() + "): "
                                            + e.getClass().getSimpleName() + " - " + e.getMessage());
                                        return Mono.empty();
                                    }));
                        }
                        return Flux.concat(increaseOps).then(saveMono);
                    }

                    return saveMono;
                }).flatMap(this::enrich);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private void validateOrigin(Payment payment) {
        boolean hasRequest = payment.getRequestId() != null;
        boolean hasItems = payment.getItems() != null && !payment.getItems().isEmpty();

        if (hasRequest && hasItems) {
            throw new IllegalArgumentException("El pago no puede tener a la vez una solicitud y libros.");
        }
        if (!hasRequest && !hasItems) {
            throw new IllegalArgumentException("El pago debe estar asociado a una solicitud o a una venta de libros.");
        }
    }

    private void serializeItems(Payment payment) {
        if (payment.getItems() != null && !payment.getItems().isEmpty()) {
            try {
                payment.setItemsDb(objectMapper.writeValueAsString(payment.getItems()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error al serializar los items del pago", e);
            }
        } else {
            payment.setItemsDb(null);
        }
    }

    private Payment deserializeItems(Payment payment) {
        if (payment.getItemsDb() != null) {
            try {
                List<BookItem> items = objectMapper.readValue(payment.getItemsDb(), new TypeReference<List<BookItem>>() {});
                payment.setItems(items);
            } catch (JsonProcessingException e) {
                System.err.println("Error al deserializar itemsDb: " + e.getMessage());
            }
        }
        return payment;
    }

    // ── Enriquecimiento de datos ──────────────────────────────────────────────

    private Mono<Payment> enrich(Payment payment) {
        Mono<Payment> enriched = Mono.just(payment);

        // Enriquecer Solicitud
        if (payment.getRequestId() != null) {
            enriched = enriched.flatMap(p -> requestClient.findById(p.getRequestId())
                    .map(info -> { p.setRequest(info); return p; })
                    .onErrorResume(e -> {
                        System.err.println("Error obteniendo Request " + p.getRequestId() + ": " + e.getMessage());
                        return Mono.just(p);
                    }));
        }

        // Enriquecer Persona
        if (payment.getPeopleId() != null) {
            enriched = enriched.flatMap(p -> peopleClient.findById(p.getPeopleId())
                    .map(info -> { p.setPeople(info); return p; })
                    .onErrorResume(e -> {
                        System.err.println("Error obteniendo People " + p.getPeopleId() + ": " + e.getMessage());
                        return Mono.just(p);
                    }));
        }

        // Enriquecer Ítems de Libros
        enriched = enriched.flatMap(p -> {
            if (p.getItems() != null && !p.getItems().isEmpty()) {
                List<Mono<BookItem>> enrichedItems = new ArrayList<>();
                for (BookItem item : p.getItems()) {
                    enrichedItems.add(
                            bookClient.findById(item.getBookId())
                                    .map(bookInfo -> {
                                        item.setBook(bookInfo);
                                        return item;
                                    })
                                    .onErrorResume(e -> Mono.just(item)) // Ignorar si falla uno
                    );
                }
                return Flux.merge(enrichedItems).collectList().map(list -> p);
            }
            return Mono.just(p);
        });

        return enriched;
    }
}
