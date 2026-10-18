package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity.PaymentEntity;

/**
 * Repositorio Reactivo de Persistencia (R2DBC).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/out/persistence/r2dbc/repository/).
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
