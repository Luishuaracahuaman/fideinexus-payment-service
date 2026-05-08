package vallegrande.edu.pe.paymentService.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vallegrande.edu.pe.paymentService.model.PaymentType;
import vallegrande.edu.pe.paymentService.service.PaymentTypeService;

import java.util.List;

@RestController
@RequestMapping("/payment-type")
public class PaymentTypeRest {

    @Autowired
    private PaymentTypeService service;

    @GetMapping
    public List<PaymentType> findAll() {
        return service.findAll();
    }

    @PostMapping
    public PaymentType save(@RequestBody PaymentType paymentType) {
        return service.save(paymentType);
    }

    @PutMapping("/{id}")
    public PaymentType update(@PathVariable Long id, @RequestBody PaymentType paymentType) {
        paymentType.setId(id);
        return service.save(paymentType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}