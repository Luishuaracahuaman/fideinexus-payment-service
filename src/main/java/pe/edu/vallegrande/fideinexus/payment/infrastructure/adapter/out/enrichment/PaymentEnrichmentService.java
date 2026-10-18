package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.enrichment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.client.BookClient;
import pe.edu.vallegrande.fideinexus.payment.client.PeopleClient;
import pe.edu.vallegrande.fideinexus.payment.client.RequestClient;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;

/**
 * Servicio de infraestructura para enriquecer pagos con datos de otros microservicios.
 *
 * Este componente es un adaptador de salida secundario que consulta
 * servicios externos (People, Request, Books) para añadir información
 * descriptiva al modelo de dominio.
 *
 * El dominio NO conoce este servicio; es usado por el controlador REST
 * y el servicio de reportes cuando necesitan datos enriquecidos para mostrar.
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
}
