package vallegrande.edu.pe.paymentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vallegrande.edu.pe.paymentService.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}