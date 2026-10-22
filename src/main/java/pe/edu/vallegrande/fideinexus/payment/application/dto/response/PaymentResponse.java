package pe.edu.vallegrande.fideinexus.payment.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long personaId;
    private Long requestReferenceId;
    private Long serviceId;
    private BigDecimal monto;
    private String metodoPago;
    private String paymentMethod;
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

    public Long getPeopleId() { return personaId; }
    public Long getRequestId() { return serviceId != null ? serviceId : requestReferenceId; }
    public BigDecimal getAmount() { return monto; }
    public LocalDateTime getPaymentDate() { return validationDate; }
}
