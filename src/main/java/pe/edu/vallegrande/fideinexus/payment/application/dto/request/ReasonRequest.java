package pe.edu.vallegrande.fideinexus.payment.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
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
public class ReasonRequest {

    @NotBlank(message = "El motivo o justificación es obligatorio")
    @Size(min = 3, max = 500, message = "El motivo debe tener entre 3 y 500 caracteres")
    @JsonAlias({"motivo", "cancelReason", "cancel_reason", "justificacion"})
    private String reason;
}
