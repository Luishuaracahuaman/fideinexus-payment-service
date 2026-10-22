package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.out.PaymentRepositoryPort;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity.PaymentEntity;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.mapper.PaymentPersistenceMapper;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.repository.PaymentJpaRepository;

/**
 * Adaptador de Salida de Persistencia (Driven Adapter).
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/out/persistence/r2dbc/).
 * 
 * Responsabilidades:
 * 1. Implementa el puerto de salida PaymentRepositoryPort definido por el Dominio.
 * 2. Delega la transformación limpia entre Dominio y Entidad a PaymentPersistenceMapper.
 * 3. Ejecuta operaciones reactivas contra la base de datos PostgreSQL mediante Spring Data R2DBC.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentPersistenceMapper persistenceMapper;

    @Override
    public Mono<Payment> save(Payment payment) {
        PaymentEntity entity = persistenceMapper.toEntity(payment);
        return paymentJpaRepository.save(entity)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Mono<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Flux<Payment> findAll() {
        return paymentJpaRepository.findAllByOrderByIdDesc()
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Flux<Payment> findByTenantId(Long tenantId) {
        return paymentJpaRepository.findByTenantIdOrderByIdDesc(tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Flux<Payment> findByPersonaId(Long personaId) {
        return paymentJpaRepository.findByPersonaIdOrderByIdDesc(personaId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Flux<Payment> findByRequestReferenceId(Long requestReferenceId) {
        return paymentJpaRepository.findByRequestReferenceIdOrderByIdDesc(requestReferenceId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Flux<Payment> findByStatus(PaymentStatus status) {
        if (status == null) {
            return Flux.empty();
        }
        return paymentJpaRepository.findByStatusOrderByIdDesc(status.toDbValue())
                .map(persistenceMapper::toDomain);
    }
}
