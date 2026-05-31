package vallegrande.edu.pe.paymentservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.paymentservice.model.Payment;

public interface PaymentRepository extends R2dbcRepository<Payment, Long> {

    Flux<Payment> findByTenantId(Long tenantId);

    Flux<Payment> findByTenantIdAndEstado(Long tenantId, String estado);
}
