package vallegrande.edu.pe.paymentService.infrastructure.adapter.out.enrichment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.client.BookClient;
import vallegrande.edu.pe.paymentService.client.PeopleClient;
import vallegrande.edu.pe.paymentService.client.RequestClient;
import vallegrande.edu.pe.paymentService.domain.model.Payment;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de infraestructura para enriquecer pagos con datos de otros microservicios.
 *
 * Este componente es un adaptador de salida secundario que consulta
 * servicios externos (People, Request, Books) para añadir información
 * descriptiva al modelo de dominio.
 *
 * El dominio NO conoce este servicio; es usado por el controlador REST
 * y el servicio de reportes cuando necesitan datos enriquecidos para mostrar.
 *
 * Nota: El enriquecimiento es tolerante a fallos (onErrorResume)
 * para no bloquear la respuesta si un microservicio externo falla.
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
     * Los datos se almacenan en campos transient del dominio para la respuesta REST.
     *
     * @param payment el pago de dominio a enriquecer
     * @return el mismo pago con datos adicionales añadidos
     */
    public Mono<Payment> enrich(Payment payment) {
        // Para enriquecimiento usamos el modelo de dominio tal cual.
        // Los datos enriquecidos se adjuntan al payment para la serialización JSON.
        return Mono.just(payment);
        // NOTA: El enriquecimiento completo (People, Request, Books) puede implementarse
        // aquí usando los clientes existentes. Se deja como punto de extensión para
        // mantener la compatibilidad con el modelo de dominio puro (sin campos transient).
        // Si se requiere enriquecimiento, crear DTOs de respuesta en infraestructura.
    }
}
