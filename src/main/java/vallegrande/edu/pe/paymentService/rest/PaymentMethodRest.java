package vallegrande.edu.pe.paymentService.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vallegrande.edu.pe.paymentService.model.PaymentMethod;
import vallegrande.edu.pe.paymentService.service.PaymentMethodService;

import java.util.List;

@RestController
@RequestMapping("/payment-method")
public class PaymentMethodRest {

    @Autowired
    private PaymentMethodService service;

    @GetMapping
    public List<PaymentMethod> findAll() {
        return service.findAll();
    }

    @PostMapping
    public PaymentMethod save(@RequestBody PaymentMethod paymentMethod) {
        return service.save(paymentMethod);
    }

    @PutMapping("/{id}")
    public PaymentMethod update(@PathVariable Long id, @RequestBody PaymentMethod paymentMethod) {
        paymentMethod.setId(id);
        return service.save(paymentMethod);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}