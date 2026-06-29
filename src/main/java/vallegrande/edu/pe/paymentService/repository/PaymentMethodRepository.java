package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import vallegrande.edu.pe.paymentService.model.PaymentMethod;

public interface PaymentMethodRepository extends R2dbcRepository<PaymentMethod, Integer> {
}
