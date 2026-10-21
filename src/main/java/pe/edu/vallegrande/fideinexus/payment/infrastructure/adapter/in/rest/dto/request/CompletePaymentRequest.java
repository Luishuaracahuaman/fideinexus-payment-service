package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.in.rest.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * DTO de entrada para completar o confirmar un pago desde el Frontend de Finanzas.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/in/rest/dto/request/)
 * 
 * Soporta payloads de confirmación simples: { "confirmedBy": 1 }
 * así como payloads completos con método de pago y voucher.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletePaymentRequest {

    /** Método de pago asignado por el operador de finanzas */
    @JsonAlias({"metodo_pago", "paymentMethod", "payment_method"})
    private String metodoPago;

    /** Código de referencia o número de voucher/operación */
    @JsonAlias({"voucher", "reference_number", "referencia"})
    private String reference;

    /** Identificador del usuario que completa/confirma la operación */
    @JsonAlias({"confirmed_by", "userId", "user_id", "confirmedBy"})
    private Long confirmedBy;

    // Métodos alias para compatibilidad programática interna
    public String getPaymentMethod() {
        return metodoPago;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.metodoPago = paymentMethod;
    }
}
