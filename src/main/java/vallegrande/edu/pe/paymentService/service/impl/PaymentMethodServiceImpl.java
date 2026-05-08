package vallegrande.edu.pe.paymentService.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vallegrande.edu.pe.paymentService.model.PaymentMethod;
import vallegrande.edu.pe.paymentService.repository.PaymentMethodRepository;
import vallegrande.edu.pe.paymentService.service.PaymentMethodService;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    private PaymentMethodRepository repository;

    @Override
    public List<PaymentMethod> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PaymentMethod> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        return repository.save(paymentMethod);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}