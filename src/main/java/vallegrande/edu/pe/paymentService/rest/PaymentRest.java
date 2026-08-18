package vallegrande.edu.pe.paymentService.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')") // Solo el admin ve todos los pagos
    public Flux<Payment> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Cualquier usuario logueado
    public Mono<Payment> findById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("isAuthenticated()")
    public Flux<Payment> findByTenant(@PathVariable Long tenantId) {
        return paymentService.findByTenant(tenantId);
    }

    @GetMapping("/people/{peopleId}")
    @PreAuthorize("isAuthenticated()")
    public Flux<Payment> findByPeople(@PathVariable Long peopleId) {
        return paymentService.findByPeople(peopleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CAJERO') or hasRole('ADMIN')")
    public Mono<Payment> save(@RequestBody Payment payment) {
        return paymentService.create(payment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Payment> update(@PathVariable Long id, @RequestBody Payment payment) {
        return paymentService.update(id, payment);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('CAJERO') or hasRole('ADMIN')")
    public Mono<Payment> confirm(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long confirmedBy = body.get("confirmedBy");
        return paymentService.confirm(id, confirmedBy);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CAJERO') or hasRole('ADMIN')")
    public Mono<Payment> cancel(@PathVariable Long id, @RequestBody CancelRequestDto cancelRequest) {
        return paymentService.cancel(id, cancelRequest);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Payment> reject(@PathVariable Long id, @RequestBody CancelRequestDto dto) {
        return paymentService.reject(id, dto);
    }

    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Payment> refund(@PathVariable Long id, @RequestBody CancelRequestDto dto) {
        return paymentService.refund(id, dto);
    }
}