package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para la creación inicial del pago.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/request/)
 * 
 * Recibe las solicitudes originadas en el frontend de Angular u otros microservicios.
 * Soporta exactamente el payload enviado por el frontend:
 * { "amount": 100, "paymentMethod": "YAPE", "peopleId": 32, "reference": "n4534", "requestId": 3, "status": "POR CONFIRMAR", "tenantId": 9 }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePaymentRequest {

    /** ID del inquilino u organización (ej. parroquia) */
    @JsonAlias({"tenant_id"})
    private Long tenantId;

    /** ID de la persona asociada al cobro */
    @JsonAlias({"persona_id", "peopleId", "people_id"})
    private Long personaId;

    /** ID del servicio o solicitud generadora del pago (acepta requestId, serviceId, etc.) */
    @JsonAlias({"requestId", "request_id", "requestReferenceId", "request_reference_id", "service_id"})
    private Long serviceId;

    /** Monto a pagar */
    @JsonAlias({"amount"})
    private BigDecimal monto;

    /** Método de pago (YAPE, PLIN, EFECTIVO, TRANSFERENCIA, etc.) */
    @JsonAlias({"metodo_pago", "metodoPago", "payment_method"})
    private String paymentMethod;

    /** Referencia o número de operación / voucher */
    @JsonAlias({"referencia", "voucher", "reference_number"})
    private String reference;

    /** Estado inicial (ej: "POR CONFIRMAR", "PENDING") */
    @JsonAlias({"estado", "payment_status"})
    private String status;

    /** Lista de items de libros (en caso aplique venta directa) */
    private List<BookItem> items;

    // Métodos de conveniencia para máxima compatibilidad programática interna
    public Long getPeopleId() {
        return personaId;
    }

    public void setPeopleId(Long peopleId) {
        this.personaId = peopleId;
    }

    public Long getRequestId() {
        return serviceId;
    }

    public void setRequestId(Long requestId) {
        this.serviceId = requestId;
    }

    public Long getRequestReferenceId() {
        return serviceId;
    }

    public void setRequestReferenceId(Long requestReferenceId) {
        this.serviceId = requestReferenceId;
    }

    public BigDecimal getAmount() {
        return monto;
    }

    public void setAmount(BigDecimal amount) {
        this.monto = amount;
    }

    public String getMetodoPago() {
        return paymentMethod;
    }

    public void setMetodoPago(String metodoPago) {
        this.paymentMethod = metodoPago;
    }
}
