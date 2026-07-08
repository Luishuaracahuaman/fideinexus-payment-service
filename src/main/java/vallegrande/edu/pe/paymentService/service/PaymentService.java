package vallegrande.edu.pe.paymentService.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.dto.CancelRequestDto;
import vallegrande.edu.pe.paymentService.model.Payment;

public interface PaymentService {

    Flux<Payment> findAll();
    
    Mono<Payment> findById(Long id);
    
    Flux<Payment> findByTenant(Long tenantId);
    
    Flux<Payment> findByPeople(Long peopleId);
    
    Mono<Payment> create(Payment payment);
    
    Mono<Payment> update(Long id, Payment payment);
    
    Mono<Payment> confirm(Long id, Long confirmedBy);
    
    Mono<Payment> cancel(Long id, CancelRequestDto cancelRequest);

    Mono<Payment> reject(Long id, CancelRequestDto dto);

    Mono<Payment> refund(Long id, CancelRequestDto dto);
}
