package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.response;

import lombok.Data;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida para respuestas REST de pagos.
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/response/)
 */
@Data
public class PaymentResponse {

    private Long id;
    private Long personaId;
    private Long requestReferenceId;
    private BigDecimal monto;
    private String metodoPago;
    private String status;
    private LocalDateTime validationDate;
    private Long tenantId;
    private String reference;
    private String cancelReason;
    private Long confirmedBy;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BookItem> items;

    // Aliases para máxima compatibilidad con el frontend
    public Long getPeopleId() { return personaId; }
    public Long getRequestId() { return requestReferenceId; }
    public BigDecimal getAmount() { return monto; }
    public String getPaymentMethod() { return metodoPago; }
    public LocalDateTime getPaymentDate() { return validationDate; }
}
