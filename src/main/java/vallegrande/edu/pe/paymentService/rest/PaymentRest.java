package vallegrande.edu.pe.paymentService.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentRest {

    @Autowired
    private PaymentService service;

    @GetMapping
    public List<Payment> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Payment save(@RequestBody Payment payment) {
        return service.save(payment);
    }

    @PutMapping("/{id}")
    public Payment update(@PathVariable Long id, @RequestBody Payment payment) {
        return service.update(id, payment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}