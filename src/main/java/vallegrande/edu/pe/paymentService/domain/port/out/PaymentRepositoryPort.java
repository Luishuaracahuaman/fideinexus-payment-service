package vallegrande.edu.pe.paymentService.domain.port.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.domain.model.Payment;
import vallegrande.edu.pe.paymentService.domain.vo.PaymentStatus;

/**
 * Puerto de Salida (Driven Port) para persistencia reactiva.
 * 
 * Regla de Arquitectura Hexagonal:
 * - Define el contrato que el dominio y aplicación necesitan para guardar y consultar pagos.
 * - Es completamente agnóstico de R2DBC, PostgreSQL, JPA o SQL.
 */
public interface PaymentRepositoryPort {

    /**
     * Guarda o actualiza un pago en el almacén de datos.
     *
     * @param payment Modelo de dominio a persistir
     * @return Mono con el pago guardado y con ID asignado si es nuevo
     */
    Mono<Payment> save(Payment payment);

    /**
     * Recupera un pago por su identificador único.
     *
     * @param id Identificador del pago
     * @return Mono con el pago o Mono.empty() si no existe
     */
    Mono<Payment> findById(Long id);

    /**
     * Obtiene todos los pagos registrados.
     */
    Flux<Payment> findAll();

    /**
     * Busca los pagos asociados a un tenant/parroquia.
     */
    Flux<Payment> findByTenantId(Long tenantId);

    /**
     * Busca los pagos asociados a una persona.
     */
    Flux<Payment> findByPersonaId(Long personaId);

    default Flux<Payment> findByPeopleId(Long peopleId) {
        return findByPersonaId(peopleId);
    }

    /**
     * Busca los pagos asociados a una solicitud de origen externa.
     */
    Flux<Payment> findByRequestReferenceId(Long requestReferenceId);

    default Flux<Payment> findByRequestId(Long requestId) {
        return findByRequestReferenceId(requestId);
    }

    /**
     * Busca los pagos según su estado de ciclo de vida (ej: PENDING, COMPLETED, CANCELLED).
     */
    Flux<Payment> findByStatus(PaymentStatus status);

    default Flux<Payment> findByStatus(String status) {
        return findByStatus(PaymentStatus.fromString(status));
    }
}
