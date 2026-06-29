package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import vallegrande.edu.pe.paymentService.model.Reason;

public interface ReasonRepository extends R2dbcRepository<Reason, Integer> {
}
