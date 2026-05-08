package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vallegrande.edu.pe.paymentService.model.PaymentType;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {
}