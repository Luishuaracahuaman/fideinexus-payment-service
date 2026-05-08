package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vallegrande.edu.pe.paymentService.model.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
}