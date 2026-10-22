package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity.PaymentEntity;

import java.util.List;

/**
 * Mapper de Persistencia (Driven Adapter Boundary).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/out/persistence/r2dbc/mapper/).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentPersistenceMapper {

    private final ObjectMapper objectMapper;

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

    private String serializeItems(List<Payment.BookItem> items) {
        if (items == null || items.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            log.error("[PERSISTENCE MAPPER] Error serializando items de pago: {}", e.getMessage());
            throw new RuntimeException("Error al serializar items de pago", e);
        }
    }

    private List<Payment.BookItem> deserializeItems(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<Payment.BookItem>>() {});
        } catch (JsonProcessingException e) {
            log.error("[PERSISTENCE MAPPER] Error deserializando items de pago: {}", e.getMessage());
            return null;
        }
    }
}
