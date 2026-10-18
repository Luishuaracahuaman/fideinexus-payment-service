package pe.edu.vallegrande.fideinexus.payment.domain.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;

/**
 * Puerto de ENTRADA (driving port) para consultas de pagos.
 *
 * Expone operaciones de lectura usadas por el controlador REST
 * y otros adaptadores que necesiten consultar el estado de los pagos.
 */
public interface QueryPaymentUseCase {

    /** Retorna todos los pagos del sistema */
    Flux<Payment> findAll();

    /** Busca un pago por su ID */
    Mono<Payment> findById(Long id);

    /** Busca todos los pagos de un tenant específico */
    Flux<Payment> findByTenantId(Long tenantId);

    /** Busca todos los pagos asociados a una persona */
    Flux<Payment> findByPeopleId(Long peopleId);

    /** Busca los pagos filtrados por estado (ej: PENDING, COMPLETED, CANCELLED) */
    Flux<Payment> findByStatus(String status);
}
