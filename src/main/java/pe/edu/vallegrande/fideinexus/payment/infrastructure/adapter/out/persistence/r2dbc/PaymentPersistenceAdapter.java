package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.out.PaymentRepositoryPort;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity.PaymentEntity;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.repository.PaymentJpaRepository;

import java.util.List;

/**
 * Adaptador de Salida de Persistencia (Driven Adapter).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/out/persistence/r2dbc/).
 * 
 * Responsabilidades:
 * 1. Implementa el puerto de salida {@link PaymentRepositoryPort} definido por el Dominio.
 * 2. Transforma bidireccionalmente entre el modelo de Dominio puro (Payment) y la entidad reactiva (PaymentEntity).
 * 3. Aísla completamente el dominio de los detalles de Spring Data R2DBC, PostgreSQL y Jackson.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Payment> save(Payment payment) {
        PaymentEntity entity = toEntity(payment);
        return paymentJpaRepository.save(entity)
                .map(this::toDomain);
    }

    @Override
    public Mono<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<Payment> findAll() {
        return paymentJpaRepository.findAllByOrderByIdDesc()
                .map(this::toDomain);
    }

    @Override
    public Flux<Payment> findByTenantId(Long tenantId) {
        return paymentJpaRepository.findByTenantIdOrderByIdDesc(tenantId)
                .map(this::toDomain);
    }

    @Override
    public Flux<Payment> findByPersonaId(Long personaId) {
        return paymentJpaRepository.findByPersonaIdOrderByIdDesc(personaId)
                .map(this::toDomain);
    }

    @Override
    public Flux<Payment> findByRequestReferenceId(Long requestReferenceId) {
        return paymentJpaRepository.findByRequestReferenceIdOrderByIdDesc(requestReferenceId)
                .map(this::toDomain);
    }

    @Override
    public Flux<Payment> findByStatus(PaymentStatus status) {
        if (status == null) {
            return Flux.empty();
        }
        return paymentJpaRepository.findByStatusOrderByIdDesc(status.toDbValue())
                .map(this::toDomain);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapeo: Dominio (Payment) → Entidad de Persistencia (PaymentEntity)
    // ─────────────────────────────────────────────────────────────────────────

    public PaymentEntity toEntity(Payment domain) {
        if (domain == null) return null;

        PaymentEntity entity = new PaymentEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setPersonaId(domain.getPersonaId());
        entity.setRequestReferenceId(domain.getRequestReferenceId());
        entity.setMonto(domain.getMonto());
        entity.setMetodoPago(domain.getMetodoPago());
        entity.setReference(domain.getReference());
        entity.setValidationDate(domain.getValidationDate());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().toDbValue() : null);
        entity.setCancelReason(domain.getCancelReason());
        entity.setConfirmedBy(domain.getConfirmedBy());
        entity.setConfirmedAt(domain.getConfirmedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setItemsJson(serializeItems(domain.getItems()));
        return entity;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapeo: Entidad de Persistencia (PaymentEntity) → Dominio (Payment)
    // ─────────────────────────────────────────────────────────────────────────

    public Payment toDomain(PaymentEntity entity) {
        if (entity == null) return null;

        Payment domain = new Payment();
        domain.setId(entity.getId());
        domain.setTenantId(entity.getTenantId());
        domain.setPersonaId(entity.getPersonaId());
        domain.setRequestReferenceId(entity.getRequestReferenceId());
        domain.setMonto(entity.getMonto());
        domain.setMetodoPago(entity.getMetodoPago());
        domain.setReference(entity.getReference());
        domain.setValidationDate(entity.getValidationDate());
        domain.setStatus(PaymentStatus.fromString(entity.getStatus()));
        domain.setCancelReason(entity.getCancelReason());
        domain.setConfirmedBy(entity.getConfirmedBy());
        domain.setConfirmedAt(entity.getConfirmedAt());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setItems(deserializeItems(entity.getItemsJson()));
        return domain;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers de serialización de items (JSON ↔ List<BookItem>)
    // ─────────────────────────────────────────────────────────────────────────

    private String serializeItems(List<Payment.BookItem> items) {
        if (items == null || items.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            log.error("[PERSISTENCE] Error serializando items de pago: {}", e.getMessage());
            throw new RuntimeException("Error al serializar items de pago", e);
        }
    }

    private List<Payment.BookItem> deserializeItems(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<Payment.BookItem>>() {});
        } catch (JsonProcessingException e) {
            log.error("[PERSISTENCE] Error deserializando items de pago: {}", e.getMessage());
            return null;
        }
    }
}
