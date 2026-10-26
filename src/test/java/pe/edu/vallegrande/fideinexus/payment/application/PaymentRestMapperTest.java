package pe.edu.vallegrande.fideinexus.payment.application.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.fideinexus.payment.application.dto.request.CreatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.application.dto.request.UpdatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.application.dto.response.PaymentResponse;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRestMapperTest {

    private PaymentRestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentRestMapper();
    }

    @Test
    @DisplayName("Debe mapear CreatePaymentRequest a Payment de dominio correctamente")
    void shouldMapCreatePaymentRequestToDomain() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setTenantId(10L);
        request.setPersonaId(25L);
        request.setServiceId(101L);
        request.setMonto(new BigDecimal("99.90"));
        request.setPaymentMethod("plin");
        request.setReference("OP-12345");
        request.setStatus("PENDING");

        Payment domain = mapper.toDomain(request);

        assertNotNull(domain);
        assertEquals(10L, domain.getTenantId());
        assertEquals(25L, domain.getPersonaId());
        assertEquals(101L, domain.getRequestReferenceId());
        assertEquals(new BigDecimal("99.90"), domain.getMonto());
        assertEquals("PLIN", domain.getMetodoPago());
        assertEquals("OP-12345", domain.getReference());
        assertEquals(PaymentStatus.PENDING, domain.getStatus());
    }

    @Test
    @DisplayName("Debe mapear UpdatePaymentRequest a Payment de dominio")
    void shouldMapUpdatePaymentRequestToDomain() {
        UpdatePaymentRequest request = new UpdatePaymentRequest();
        request.setMonto(new BigDecimal("200.00"));
        request.setMetodoPago("efectivo");
        request.setReference("RECIBO-01");
        request.setCancelReason("Cliente solicitó corrección");
        request.setStatus("CANCELLED");

        Payment domain = mapper.toDomain(request);

        assertNotNull(domain);
        assertEquals(new BigDecimal("200.00"), domain.getMonto());
        assertEquals("EFECTIVO", domain.getMetodoPago());
        assertEquals("RECIBO-01", domain.getReference());
        assertEquals("Cliente solicitó corrección", domain.getCancelReason());
        assertEquals(PaymentStatus.CANCELLED, domain.getStatus());
    }

    @Test
    @DisplayName("Debe mapear Payment de dominio a PaymentResponse DTO")
    void shouldMapDomainToPaymentResponse() {
        Payment domain = new Payment();
        domain.setId(77L);
        domain.setPersonaId(25L);
        domain.setRequestReferenceId(101L);
        domain.setMonto(new BigDecimal("350.00"));
        domain.setMetodoPago("YAPE");
        domain.setStatus(PaymentStatus.COMPLETED);
        domain.setValidationDate(LocalDateTime.now());
        domain.setTenantId(5L);
        domain.setReference("REF-777");

        PaymentResponse response = mapper.toResponse(domain);

        assertNotNull(response);
        assertEquals(77L, response.getId());
        assertEquals(25L, response.getPersonaId());
        assertEquals(101L, response.getRequestReferenceId());
        assertEquals(new BigDecimal("350.00"), response.getMonto());
        assertEquals("YAPE", response.getMetodoPago());
        assertEquals("COMPLETED", response.getStatus());
        assertEquals("REF-777", response.getReference());
    }

    @Test
    @DisplayName("Debe retornar null cuando el objeto a mapear sea nulo")
    void shouldHandleNullInputsGracefully() {
        assertNull(mapper.toDomain((CreatePaymentRequest) null));
        assertNull(mapper.toDomain((UpdatePaymentRequest) null));
        assertNull(mapper.toResponse(null));
        assertEquals(Collections.emptyList(), mapper.toResponseList(null));
    }
}
