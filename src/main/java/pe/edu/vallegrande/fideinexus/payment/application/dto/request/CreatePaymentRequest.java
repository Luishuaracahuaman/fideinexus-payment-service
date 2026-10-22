package pe.edu.vallegrande.fideinexus.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class CreatePaymentRequest {

    @NotNull(message = "El tenantId es obligatorio")
    @Positive(message = "El tenantId debe ser mayor a cero")
    @JsonAlias({"tenant_id"})
    private Long tenantId;

    @NotNull(message = "El ID de la persona (personaId) es obligatorio")
    @Positive(message = "El ID de la persona debe ser mayor a cero")
    @JsonAlias({"persona_id", "peopleId", "people_id"})
    private Long personaId;

    @Positive(message = "El ID del servicio o solicitud debe ser mayor a cero")
    @JsonAlias({"requestId", "request_id", "requestReferenceId", "request_reference_id", "service_id"})
    private Long serviceId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto mínimo permitido es 0.01")
    @Positive(message = "El monto debe ser mayor a cero")
    @JsonAlias({"amount"})
    private BigDecimal monto;

    @Size(max = 50, message = "El método de pago no debe exceder los 50 caracteres")
    @JsonAlias({"metodo_pago", "metodoPago", "payment_method"})
    private String paymentMethod;

    @Size(max = 100, message = "La referencia no debe exceder los 100 caracteres")
    @JsonAlias({"referencia", "voucher", "reference_number"})
    private String reference;

    @Size(max = 50, message = "El estado no debe exceder los 50 caracteres")
    @JsonAlias({"estado", "payment_status"})
    private String status;

    private List<BookItem> items;

    public Long getPeopleId() { return personaId; }
    public void setPeopleId(Long peopleId) { this.personaId = peopleId; }

    public Long getRequestId() { return serviceId; }
    public void setRequestId(Long requestId) { this.serviceId = requestId; }

    public Long getRequestReferenceId() { return serviceId; }
    public void setRequestReferenceId(Long requestReferenceId) { this.serviceId = requestReferenceId; }

    public BigDecimal getAmount() { return monto; }
    public void setAmount(BigDecimal amount) { this.monto = amount; }

    public String getMetodoPago() { return paymentMethod; }
    public void setMetodoPago(String metodoPago) { this.paymentMethod = metodoPago; }
}
