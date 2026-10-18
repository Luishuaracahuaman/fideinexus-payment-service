package pe.edu.vallegrande.fideinexus.payment.domain.vo;

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
     * Convierte valores string (incluyendo nombres en español y legacy) al Enum del dominio.
     */
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

    public String toDbValue() {
        return this.name();
    }
}
