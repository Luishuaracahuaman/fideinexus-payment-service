package vallegrande.edu.pe.paymentService.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.dto.CancelRequestDto;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentRest {

    private final PaymentService paymentService;

    @GetMapping
    public Flux<Payment> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Payment> findById(@PathVariable Long id) {
        return paymentService.findById(id);
    }
    
    @GetMapping("/tenant/{tenantId}")
    public Flux<Payment> findByTenant(@PathVariable Long tenantId) {
        return paymentService.findByTenant(tenantId);
    }
    
    @GetMapping("/people/{peopleId}")
    public Flux<Payment> findByPeople(@PathVariable Long peopleId) {
        return paymentService.findByPeople(peopleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Payment> save(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }

    @PutMapping("/{id}")
    public Mono<Payment> update(@PathVariable Long id, @RequestBody Payment payment) {
        return paymentService.update(id, payment);
    }

    @PatchMapping("/{id}/confirm")
    public Mono<Payment> confirm(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long confirmedBy = body.get("confirmedBy");
        return paymentService.confirm(id, confirmedBy);
    }
    
    @PatchMapping("/{id}/cancel")
    public Mono<Payment> cancel(@PathVariable Long id, @RequestBody CancelRequestDto cancelRequest) {
        return paymentService.cancel(id, cancelRequest);
    }

    @PatchMapping("/{id}/reject")
    public Mono<Payment> reject(@PathVariable Long id, @RequestBody CancelRequestDto dto) {
        return paymentService.reject(id, dto);
    }

    @PatchMapping("/{id}/refund")
    public Mono<Payment> refund(@PathVariable Long id, @RequestBody CancelRequestDto dto) {
        return paymentService.refund(id, dto);
    }
}
