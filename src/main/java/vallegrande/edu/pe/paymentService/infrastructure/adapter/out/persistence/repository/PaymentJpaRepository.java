package vallegrande.edu.pe.paymentService.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.paymentService.infrastructure.adapter.out.persistence.entity.PaymentEntity;

/**
 * Repositorio Reactivo de Persistencia (R2DBC).
 *
 * Ubicación: Capa de INFRAESTRUCTURA.
 * 
 * Extiende {@link ReactiveCrudRepository} para proveer operaciones no bloqueantes (Mono/Flux).
 * El sufijo "JpaRepository" se respeta por requerimiento arquitectural, pero la implementación
 * subyacente es 100% reactiva mediante R2DBC.
 */
@Repository
public interface PaymentJpaRepository extends ReactiveCrudRepository<PaymentEntity, Long> {

    Flux<PaymentEntity> findByTenantId(Long tenantId);

    Flux<PaymentEntity> findByPersonaId(Long personaId);

    Flux<PaymentEntity> findByRequestReferenceId(Long requestReferenceId);

    Flux<PaymentEntity> findByStatus(String status);
}
