package pe.edu.vallegrande.fideinexus.payment.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Value Object de Dominio: Estados de un Pago.
 */
public enum PaymentStatus {

    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
    REJECTED;

    @JsonCreator
    public static PaymentStatus fromString(String value) {
        if (value == null || value.isBlank()) return PENDING;
        return switch (value.toUpperCase().trim().replace(" ", "_")) {
            case "PENDING", "PENDIENTE", "PENDIENTES", "POR_CONFIRMAR" -> PENDING;
            case "COMPLETED", "COMPLETADO", "COMPLETADOS", "PAGADO", "PAGADOS", "CONFIRMADO", "CONFIRMADOS" -> COMPLETED;
            case "CANCELLED", "CANCELED", "CANCELADO", "CANCELADOS", "ANULADO", "ANULADOS" -> CANCELLED;
            case "FAILED", "FALLIDO", "FALLIDOS" -> FAILED;
            case "REFUNDED", "REEMBOLSADO", "REEMBOLSADOS" -> REFUNDED;
            case "REJECTED", "RECHAZADO", "RECHAZADOS" -> REJECTED;
            default -> PENDING;
        };
    }

    @JsonValue
    public String toFrontend() {
        return switch (this) {
            case COMPLETED -> "CONFIRMADO";
            case PENDING -> "POR CONFIRMAR";
            case CANCELLED -> "ANULADO";
            case REFUNDED -> "REEMBOLSADO";
            case REJECTED -> "RECHAZADO";
            default -> this.name();
        };
    }

    public String toDbValue() {
        return this.name();
    }
}
