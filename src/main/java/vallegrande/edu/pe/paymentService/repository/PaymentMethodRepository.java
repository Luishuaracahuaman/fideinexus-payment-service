package vallegrande.edu.pe.paymentservice.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import vallegrande.edu.pe.paymentservice.model.PaymentMethod;

public interface PaymentMethodRepository extends R2dbcRepository<PaymentMethod, Integer> {
}
