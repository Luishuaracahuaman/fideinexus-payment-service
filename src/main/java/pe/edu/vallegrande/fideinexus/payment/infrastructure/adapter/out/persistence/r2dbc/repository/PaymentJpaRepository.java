package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity.PaymentEntity;

/**
 * Repositorio Reactivo de Persistencia (R2DBC).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/out/persistence/r2dbc/repository/).
 * 
 * Extiende {@link ReactiveCrudRepository} y {@link ReactiveSortingRepository} para proveer
 * operaciones no bloqueantes (Mono/Flux) y ordenamiento descendente por ID.
 */
@Repository
public interface PaymentJpaRepository extends ReactiveCrudRepository<PaymentEntity, Long>, ReactiveSortingRepository<PaymentEntity, Long> {

    Flux<PaymentEntity> findAllByOrderByIdDesc();

    Flux<PaymentEntity> findByTenantIdOrderByIdDesc(Long tenantId);

    Flux<PaymentEntity> findAllByTenantIdOrderByIdDesc(Long tenantId);

    Flux<PaymentEntity> findByPersonaIdOrderByIdDesc(Long personaId);

    Flux<PaymentEntity> findByRequestReferenceIdOrderByIdDesc(Long requestReferenceId);

    Flux<PaymentEntity> findByStatusOrderByIdDesc(String status);

    Flux<PaymentEntity> findByTenantId(Long tenantId, Sort sort);

    Flux<PaymentEntity> findByPersonaId(Long personaId, Sort sort);

    Flux<PaymentEntity> findByStatus(String status, Sort sort);
}
