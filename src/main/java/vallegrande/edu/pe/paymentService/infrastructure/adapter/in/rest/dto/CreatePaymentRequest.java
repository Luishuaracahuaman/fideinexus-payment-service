package vallegrande.edu.pe.paymentService.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import vallegrande.edu.pe.paymentService.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para la creación inicial del pago.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/)
 * 
 * Recibe las solicitudes originadas en otros microservicios (Sacramentos, Libros, etc.).
 * Gracias a @JsonAlias soporta tanto notación snake_case (persona_id, request_id)
 * como camelCase o términos en inglés para máxima compatibilidad.
 */
@Data
public class CreatePaymentRequest {

    /** ID del inquilino u organización (ej. parroquia) */
    @JsonAlias({"tenant_id"})
    private Long tenantId;

    /** ID de la persona asociada al cobro */
    @JsonAlias({"persona_id", "peopleId", "people_id"})
    private Long personaId;

    /** ID o referencia de la solicitud generadora del pago en el otro microservicio */
    @JsonAlias({"request_id", "requestId", "request_reference_id"})
    private Long requestReferenceId;

    /** Monto a pagar */
    @JsonAlias({"amount"})
    private BigDecimal monto;

    /** Lista de items de libros (en caso aplique venta directa) */
    private List<BookItem> items;

    // Métodos de conveniencia para compatibilidad programática interna
    public Long getPeopleId() {
        return personaId;
    }

    public void setPeopleId(Long peopleId) {
        if (this.personaId == null) {
            this.personaId = peopleId;
        }
    }

    public Long getRequestId() {
        return requestReferenceId;
    }

    public void setRequestId(Long requestId) {
        if (this.requestReferenceId == null) {
            this.requestReferenceId = requestId;
        }
    }

    public BigDecimal getAmount() {
        return monto;
    }

    public void setAmount(BigDecimal amount) {
        if (this.monto == null) {
            this.monto = amount;
        }
    }
}
