package vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * DTO de entrada para operaciones de ciclo de vida con justificación (cancelar, rechazar, reembolsar).
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/)
 */
@Data
public class ReasonRequest {

    /** Motivo o justificación de la acción sobre el pago */
    @JsonAlias({"motivo", "cancelReason", "cancel_reason", "justificacion"})
    private String reason;
}
