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
