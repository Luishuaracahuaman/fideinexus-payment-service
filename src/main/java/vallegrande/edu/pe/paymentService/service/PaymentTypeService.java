package vallegrande.edu.pe.paymentService.service;

import vallegrande.edu.pe.paymentService.model.PaymentType;
import java.util.List;
import java.util.Optional;

public interface PaymentTypeService {
    List<PaymentType> findAll();

    Optional<PaymentType> findById(Long id);

    PaymentType save(PaymentType paymentType);

    void deleteById(Long id);
}