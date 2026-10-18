package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * DTO de entrada para operaciones de ciclo de vida con justificación (cancelar, rechazar, reembolsar).
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/request/)
 */
@Data
public class ReasonRequest {

    /** Motivo o justificación de la acción sobre el pago */
    @JsonAlias({"motivo", "cancelReason", "cancel_reason", "justificacion"})
    private String reason;
}
