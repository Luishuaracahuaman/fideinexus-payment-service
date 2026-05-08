package vallegrande.edu.pe.paymentService.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vallegrande.edu.pe.paymentService.model.PaymentType;
import vallegrande.edu.pe.paymentService.repository.PaymentTypeRepository;
import vallegrande.edu.pe.paymentService.service.PaymentTypeService;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentTypeServiceImpl implements PaymentTypeService {

    @Autowired
    private PaymentTypeRepository repository;

    @Override
    public List<PaymentType> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PaymentType> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public PaymentType save(PaymentType paymentType) {
        return repository.save(paymentType);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}