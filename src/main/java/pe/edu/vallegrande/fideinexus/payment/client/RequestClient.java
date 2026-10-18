package pe.edu.vallegrande.fideinexus.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.dto.RequestInfo;

@Component
public class RequestClient {

    private final WebClient webClient;

    public RequestClient(WebClient.Builder builder, @Value("${external.services.requests.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public Mono<RequestInfo> findById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(RequestInfo.class);
    }
}
