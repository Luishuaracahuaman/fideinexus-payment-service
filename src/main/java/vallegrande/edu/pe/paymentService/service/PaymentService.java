package vallegrande.edu.pe.paymentService.service;

import vallegrande.edu.pe.paymentService.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> findAll();

    Optional<Payment> findById(Long id);

    Payment save(Payment payment);

    Payment update(Long id, Payment payment);

    void deleteById(Long id);
}