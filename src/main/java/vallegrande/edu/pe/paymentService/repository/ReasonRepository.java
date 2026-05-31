package vallegrande.edu.pe.paymentservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import vallegrande.edu.pe.paymentservice.model.Reason;

public interface ReasonRepository extends R2dbcRepository<Reason, Integer> {
}
