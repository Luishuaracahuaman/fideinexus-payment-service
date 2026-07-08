package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.paymentService.model.Payment;

@Repository
public interface PaymentRepository extends R2dbcRepository<Payment, Long> {

    Flux<Payment> findByStatus(String status);
    
    Flux<Payment> findByTenantId(Long tenantId);
    
    Flux<Payment> findByPeopleId(Long peopleId);
    
    Flux<Payment> findByRequestId(Long requestId);
}
