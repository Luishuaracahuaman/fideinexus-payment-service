package vallegrande.edu.pe.paymentService.domain.port.in;

import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.domain.model.Payment;

/**
 * Puerto de Entrada (Driving Port) para la creación inicial de un pago.
 * 
 * Invocado por adaptadores REST u otros canales cuando un microservicio
 * externo (sacramentos, servicios, etc.) envía un pago a centralizar
 * con monto, personaId y requestReferenceId.
 */
public interface CreatePaymentUseCase {

    /**
     * Registra el pago en estado PENDING para ser completado posteriormente por finanzas.
     *
     * @param payment Modelo de dominio con los datos iniciales
     * @return Mono con el pago persistido en estado PENDING
     */
    Mono<Payment> createPayment(Payment payment);
}
