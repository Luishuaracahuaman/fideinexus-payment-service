package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.CompletePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.CreatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request.UpdatePaymentRequest;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.response.PaymentResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper de la capa REST para convertir entre DTOs y el modelo de dominio Payment.
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/mapper/).
 */
@Component
public class PaymentRestMapper {

    /**
     * Mapea un CreatePaymentRequest al modelo de dominio Payment.
     */
    public Payment toDomain(CreatePaymentRequest request) {
        if (request == null) return null;
        Payment payment = new Payment();
        payment.setTenantId(request.getTenantId());
        payment.setPersonaId(request.getPersonaId() != null ? request.getPersonaId() : request.getPeopleId());
        payment.setRequestReferenceId(request.getRequestReferenceId() != null ? request.getRequestReferenceId() : request.getRequestId());
        payment.setMonto(request.getMonto() != null ? request.getMonto() : request.getAmount());
        payment.setItems(request.getItems());
        return payment;
    }

    /**
     * Mapea un UpdatePaymentRequest al modelo de dominio Payment (parcial).
     */
    public Payment toDomain(UpdatePaymentRequest request) {
        if (request == null) return null;
        Payment payment = new Payment();
        payment.setMonto(request.getMonto() != null ? request.getMonto() : request.getAmount());
        payment.setPersonaId(request.getPersonaId() != null ? request.getPersonaId() : request.getPeopleId());
        payment.setRequestReferenceId(request.getRequestReferenceId() != null ? request.getRequestReferenceId() : request.getRequestId());
        payment.setMetodoPago(request.getMetodoPago() != null ? request.getMetodoPago() : request.getPaymentMethod());
        payment.setReference(request.getReference());
        payment.setTenantId(request.getTenantId());
        payment.setCancelReason(request.getCancelReason());
        payment.setConfirmedBy(request.getConfirmedBy());
        payment.setItems(request.getItems());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            payment.setStatus(PaymentStatus.fromString(request.getStatus()));
        }
        return payment;
    }

    /**
     * Mapea el modelo de dominio Payment al DTO PaymentResponse.
     */
    public PaymentResponse toResponse(Payment domain) {
        if (domain == null) return null;
        PaymentResponse response = new PaymentResponse();
        response.setId(domain.getId());
        response.setPersonaId(domain.getPersonaId());
        response.setRequestReferenceId(domain.getRequestReferenceId());
        response.setMonto(domain.getMonto());
        response.setMetodoPago(domain.getMetodoPago());
        response.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        response.setValidationDate(domain.getValidationDate());
        response.setTenantId(domain.getTenantId());
        response.setReference(domain.getReference());
        response.setCancelReason(domain.getCancelReason());
        response.setConfirmedBy(domain.getConfirmedBy());
        response.setConfirmedAt(domain.getConfirmedAt());
        response.setCreatedAt(domain.getCreatedAt());
        response.setUpdatedAt(domain.getUpdatedAt());
        response.setItems(domain.getItems());
        return response;
    }

    /**
     * Mapea una lista de modelos de dominio Payment a una lista de PaymentResponse.
     */
    public List<PaymentResponse> toResponseList(List<Payment> domainList) {
        if (domainList == null) return Collections.emptyList();
        return domainList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
