package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity.PaymentEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentPersistenceMapperTest {

    private PaymentPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentPersistenceMapper(new ObjectMapper());
    }

    @Test
    @DisplayName("Debe mapear Payment de dominio a PaymentEntity de persistencia")
    void shouldMapDomainToEntity() {
        Payment domain = new Payment();
        domain.setId(5L);
        domain.setTenantId(1L);
        domain.setPersonaId(20L);
        domain.setRequestReferenceId(50L);
        domain.setMonto(new BigDecimal("120.00"));
        domain.setMetodoPago("EFECTIVO");
        domain.setStatus(PaymentStatus.COMPLETED);
        domain.setReference("OP-8899");
        domain.setValidationDate(LocalDateTime.now());

        PaymentEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(5L, entity.getId());
        assertEquals(1L, entity.getTenantId());
        assertEquals(20L, entity.getPersonaId());
        assertEquals(50L, entity.getRequestReferenceId());
        assertEquals(new BigDecimal("120.00"), entity.getMonto());
        assertEquals("EFECTIVO", entity.getMetodoPago());
        assertEquals("COMPLETED", entity.getStatus());
        assertEquals("OP-8899", entity.getReference());
    }

    @Test
    @DisplayName("Debe mapear PaymentEntity de persistencia a Payment de dominio")
    void shouldMapEntityToDomain() {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(10L);
        entity.setTenantId(2L);
        entity.setPersonaId(30L);
        entity.setRequestReferenceId(60L);
        entity.setMonto(new BigDecimal("250.00"));
        entity.setMetodoPago("PLIN");
        entity.setStatus("PENDING");
        entity.setReference("REF-1234");
        entity.setCreatedAt(LocalDateTime.now());

        Payment domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(10L, domain.getId());
        assertEquals(2L, domain.getTenantId());
        assertEquals(30L, domain.getPersonaId());
        assertEquals(60L, domain.getRequestReferenceId());
        assertEquals(new BigDecimal("250.00"), domain.getMonto());
        assertEquals("PLIN", domain.getMetodoPago());
        assertEquals(PaymentStatus.PENDING, domain.getStatus());
        assertEquals("REF-1234", domain.getReference());
    }

    @Test
    @DisplayName("Debe retornar null cuando la entrada sea nula")
    void shouldReturnNullWhenInputIsNull() {
        assertNull(mapper.toEntity(null));
        assertNull(mapper.toDomain(null));
    }
}
