package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import vallegrande.edu.pe.paymentService.model.People;

public interface PeopleRepository extends R2dbcRepository<People, Long> {
    
    Flux<People> findByEstado(String estado);

    Flux<People> findByTenantId(Long tenantId);

    Flux<People> findByTenantIdAndEstado(Long tenantId, String estado);
}
