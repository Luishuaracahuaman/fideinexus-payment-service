package vallegrande.edu.pe.paymentService.service;

import vallegrande.edu.pe.paymentService.model.PaymentMethod;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    List<PaymentMethod> findAll();

    Optional<PaymentMethod> findById(Long id);

    PaymentMethod save(PaymentMethod paymentMethod);

    void deleteById(Long id);
}