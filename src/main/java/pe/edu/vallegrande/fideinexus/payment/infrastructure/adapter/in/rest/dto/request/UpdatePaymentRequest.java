package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de entrada para editar un pago existente desde el formulario.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/request/)
 * 
 * Permite editar los campos modificables en el formulario:
 * - Datos del pago: monto, método de pago, referencia/voucher.
 * - Estado: PENDING, COMPLETED, CANCELLED, etc.
 * - Referencias: personaId, requestReferenceId, tenantId, cancelReason, confirmedBy, items.
 */
@Data
public class UpdatePaymentRequest {

    /** Monto del pago */
    @JsonAlias({"amount"})
    private BigDecimal monto;

    /** Método de pago (EFECTIVO, YAPE, PLIN, TRANSFERENCIA, TARJETA, etc.) */
    @JsonAlias({"metodo_pago", "paymentMethod", "payment_method"})
    private String metodoPago;

    /** Estado del pago (PENDING, COMPLETED, CANCELLED, etc. o en español: PAGADO, PENDIENTE, CANCELADO) */
    @JsonAlias({"estado", "payment_status"})
    private String status;

    /** Código de comprobante, voucher o número de operación */
    @JsonAlias({"voucher", "reference_number"})
    private String reference;

    /** ID de la persona asociada */
    @JsonAlias({"persona_id", "peopleId", "people_id"})
    private Long personaId;

    /** ID de la solicitud de origen */
    @JsonAlias({"request_id", "requestId", "request_reference_id"})
    private Long requestReferenceId;

    /** ID del inquilino / parroquia */
    @JsonAlias({"tenant_id"})
    private Long tenantId;

    /** Motivo en caso de cancelación, anulación o rechazo */
    @JsonAlias({"motivo_cancelacion", "reason", "cancel_reason"})
    private String cancelReason;

    /** ID del usuario auditor que realiza la edición o confirmación */
    @JsonAlias({"user_id", "userId", "confirmed_by"})
    private Long confirmedBy;

    /** Lista de items en caso aplique venta de libros */
    private List<BookItem> items;

    // Métodos alias para compatibilidad interna
    public BigDecimal getAmount() { return monto; }
    public void setAmount(BigDecimal amount) { if (this.monto == null) this.monto = amount; }

    public String getPaymentMethod() { return metodoPago; }
    public void setPaymentMethod(String paymentMethod) { if (this.metodoPago == null) this.metodoPago = paymentMethod; }

    public Long getPeopleId() { return personaId; }
    public void setPeopleId(Long peopleId) { if (this.personaId == null) this.personaId = peopleId; }

    public Long getRequestId() { return requestReferenceId; }
    public void setRequestId(Long requestId) { if (this.requestReferenceId == null) this.requestReferenceId = requestId; }
}
