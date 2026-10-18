package vallegrande.edu.pe.paymentService.domain.vo;

/**
 * Value Object de Dominio: Estados de un Pago.
 *
 * Estados principales del ciclo de vida:
 *  - PENDING: Pago registrado por otro microservicio, pendiente de confirmación en finanzas.
 *  - COMPLETED: Pago completado desde finanzas con método de pago asignado.
 *  - FAILED: Pago fallido por inconsistencia técnica o de validación.
 *  - CANCELLED / REJECTED / REFUNDED: Flujos complementarios de anulación y reembolso.
 */
public enum PaymentStatus {

    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
    REJECTED;

    /**
     * Convierte valores string (incluyendo nombres legacy) al Enum del dominio.
     */
    public static PaymentStatus fromString(String value) {
        if (value == null) return PENDING;
        return switch (value.toUpperCase().replace(" ", "_")) {
            case "POR_CONFIRMAR", "PENDING"   -> PENDING;
            case "CONFIRMADO",   "COMPLETED"  -> COMPLETED;
            case "FALLIDO",      "FAILED"     -> FAILED;
            case "ANULADO",      "CANCELLED"  -> CANCELLED;
            case "REEMBOLSADO",  "REFUNDED"   -> REFUNDED;
            case "RECHAZADO",    "REJECTED"   -> REJECTED;
            default -> PENDING;
        };
    }

    public String toDbValue() {
        return this.name();
    }
}
