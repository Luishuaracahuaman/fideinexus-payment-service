package pe.edu.vallegrande.fideinexus.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletePaymentRequest {

    @NotBlank(message = "El método de pago es obligatorio para completar el pago")
    @Size(max = 50, message = "El método de pago no puede exceder los 50 caracteres")
    @JsonAlias({"metodo_pago", "paymentMethod", "payment_method"})
    private String metodoPago;

    @Size(max = 100, message = "La referencia no puede exceder los 100 caracteres")
    @JsonAlias({"voucher", "reference_number", "referencia"})
    private String reference;

    @NotNull(message = "El ID del usuario que confirma (confirmedBy) es obligatorio")
    @Positive(message = "El ID del usuario que confirma debe ser mayor a cero")
    @JsonAlias({"confirmed_by", "userId", "user_id", "confirmedBy"})
    private Long confirmedBy;

    public String getPaymentMethod() {
        return metodoPago;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.metodoPago = paymentMethod;
    }
}
