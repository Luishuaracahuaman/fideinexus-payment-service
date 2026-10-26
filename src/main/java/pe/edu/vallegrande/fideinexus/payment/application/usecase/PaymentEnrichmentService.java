package pe.edu.vallegrande.fideinexus.payment.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.client.BookClient;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.client.PeopleClient;
import pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.client.RequestClient;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.QueryPaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.application.dto.response.PaymentEnrichedResponse;
import pe.edu.vallegrande.fideinexus.payment.application.dto.response.PeopleInfo;

/**
 * Servicio de infraestructura para enriquecer pagos con datos de otros microservicios
 * y suministrar listas filtradas de entidades externas (feligreses, libros, solicitudes).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEnrichmentService {

    private final BookClient bookClient;
    private final RequestClient requestClient;
    private final PeopleClient peopleClient;
    private final QueryPaymentUseCase queryPaymentUseCase;

    /**
     * Enriquece un pago con datos de People, Request y Books.
     *
     * @param payment el pago de dominio a enriquecer
     * @return el mismo pago con datos adicionales añadidos
     */
    public Mono<Payment> enrich(Payment payment) {
        return Mono.just(payment);
    }

    /**
     * Implementación oficial del estándar PRS3:
     * GET /api/v1/payments/{id}/full enriquece el pago llamando a vg-ms-peopleService y vg-ms-booksService.
     */
    public Mono<PaymentEnrichedResponse> getPaymentFull(Long id) {
        return queryPaymentUseCase.findById(id)
                .flatMap(payment -> {
                    Mono<PeopleInfo> personMono = payment.getPersonaId() != null
                            ? peopleClient.findById(payment.getPersonaId()).onErrorResume(e -> Mono.empty())
                            : Mono.empty();

                    return personMono.defaultIfEmpty(new PeopleInfo())
                            .map(person -> {
                                PaymentEnrichedResponse.CustomerInfo customer = PaymentEnrichedResponse.CustomerInfo.builder()
                                        .id(person.getId())
                                        .name(person.getFullName() != null && !person.getFullName().isBlank() ? person.getFullName() : person.getName())
                                        .documentNumber(person.getDocumentNumber())
                                        .email(person.getEmail())
                                        .phone(person.getPhone())
                                        .build();

                                return PaymentEnrichedResponse.builder()
                                        .paymentId(payment.getId())
                                        .amount(payment.getMonto())
                                        .paymentMethod(payment.getMetodoPago())
                                        .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                                        .date(payment.getValidationDate() != null ? payment.getValidationDate().toString() : null)
                                        .validationDate(payment.getValidationDate())
                                        .tenantId(payment.getTenantId())
                                        .reference(payment.getReference())
                                        .customer(customer)
                                        .items(payment.getItems())
                                        .build();
                            });
                });
    }

    /**
     * Consume PeopleClient para retornar la lista filtrada de feligreses al frontend.
     * El parámetro tenantId es dinámico y se resuelve automáticamente del contexto si viene nulo.
     *
     * @param tenantId ID del tenant/parroquia (opcional)
     * @return Flux de PeopleInfo con los feligreses filtrados
     */
    public Flux<PeopleInfo> getFeligreses(Long tenantId) {
        return peopleClient.findFeligreses(tenantId);
    }
}
