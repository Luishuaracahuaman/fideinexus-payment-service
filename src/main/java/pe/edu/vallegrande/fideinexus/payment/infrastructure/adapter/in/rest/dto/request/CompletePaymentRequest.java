package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * DTO de entrada para completar un pago desde el Frontend de Finanzas.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/request/)
 * 
 * Recibe el método de pago elegido (EFECTIVO, TRANSFERENCIA, YAPE, PLIN, etc.)
 * y datos complementarios de auditoría o referencia/voucher.
 */
@Data
public class CompletePaymentRequest {

    /** Método de pago asignado por el operador de finanzas */
    @JsonAlias({"metodo_pago", "paymentMethod", "payment_method"})
    private String metodoPago;

    /** Código de referencia o número de voucher/operación */
    @JsonAlias({"voucher", "reference_number"})
    private String reference;

    /** Identificador del usuario de finanzas que completa la operación */
    @JsonAlias({"confirmed_by", "userId", "user_id"})
    private Long confirmedBy;

    // Métodos alias para compatibilidad programática interna
    public String getPaymentMethod() {
        return metodoPago;
    }

    public void setPaymentMethod(String paymentMethod) {
        if (this.metodoPago == null) {
            this.metodoPago = paymentMethod;
        }
    }
}
