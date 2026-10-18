package pe.edu.vallegrande.fideinexus.payment.domain.port.in;

import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;

/**
 * Puerto de Entrada (Driving Port) para completar y gestionar pagos.
 * 
 * Invocado por el frontend del área de Finanzas para:
 * 1. Asignar el método de pago al registro PENDING y transicionar a COMPLETED.
 * 2. Cancelar, rechazar o reembolsar operaciones según reglas de negocio.
 */
public interface CompletePaymentUseCase {

    /**
     * Completa un pago en estado PENDING asignándole el método de pago elegido en Finanzas.
     *
     * @param id         Identificador único del pago
     * @param metodoPago Método de pago asignado (ej. EFECTIVO, TRANSFERENCIA, YAPE, PLIN)
     * @return Mono con el pago actualizado en estado COMPLETED
     */
    Mono<Payment> completePayment(Long id, String metodoPago);

    /**
     * Versión extendida para completar pago con referencia de voucher y usuario auditor.
     */
    Mono<Payment> completePayment(Long id, String metodoPago, String reference, Long confirmedBy);

    /** Cancela un pago en estado PENDING */
    Mono<Payment> cancelPayment(Long id, String reason);

    /** Rechaza un pago en estado PENDING */
    Mono<Payment> rejectPayment(Long id, String reason);

    /** Reembolsa un pago en estado COMPLETED */
    Mono<Payment> refundPayment(Long id, String reason);
}
