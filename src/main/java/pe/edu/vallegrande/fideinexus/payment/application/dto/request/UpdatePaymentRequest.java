package pe.edu.vallegrande.fideinexus.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePaymentRequest {

    @DecimalMin(value = "0.01", message = "El monto debe ser como mínimo 0.01")
    @Positive(message = "El monto debe ser un valor positivo")
    @JsonAlias({"amount"})
    private BigDecimal monto;

    @Size(max = 50, message = "El método de pago no puede exceder los 50 caracteres")
    @JsonAlias({"metodo_pago", "paymentMethod", "payment_method"})
    private String metodoPago;

    @Size(max = 50, message = "El estado no puede exceder los 50 caracteres")
    @JsonAlias({"estado", "payment_status"})
    private String status;

    @Size(max = 100, message = "La referencia no puede exceder los 100 caracteres")
    @JsonAlias({"voucher", "reference_number"})
    private String reference;

    @Positive(message = "El ID de la persona debe ser mayor a cero")
    @JsonAlias({"persona_id", "peopleId", "people_id"})
    private Long personaId;

    @Positive(message = "El ID de la solicitud debe ser mayor a cero")
    @JsonAlias({"request_id", "requestId", "request_reference_id"})
    private Long requestReferenceId;

    @Positive(message = "El tenantId debe ser mayor a cero")
    @JsonAlias({"tenant_id"})
    private Long tenantId;

    @Size(max = 500, message = "El motivo no puede exceder los 500 caracteres")
    @JsonAlias({"motivo_cancelacion", "reason", "cancel_reason"})
    private String cancelReason;

    @Positive(message = "El ID del usuario auditor debe ser mayor a cero")
    @JsonAlias({"user_id", "userId", "confirmed_by"})
    private Long confirmedBy;

    private List<BookItem> items;

    public BigDecimal getAmount() { return monto; }
    public void setAmount(BigDecimal amount) { if (this.monto == null) this.monto = amount; }

    public String getPaymentMethod() { return metodoPago; }
    public void setPaymentMethod(String paymentMethod) { if (this.metodoPago == null) this.metodoPago = paymentMethod; }

    public Long getPeopleId() { return personaId; }
    public void setPeopleId(Long peopleId) { if (this.personaId == null) this.personaId = peopleId; }

    public Long getRequestId() { return requestReferenceId; }
    public void setRequestId(Long requestId) { if (this.requestReferenceId == null) this.requestReferenceId = requestId; }
}
