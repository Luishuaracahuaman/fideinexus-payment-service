package pe.edu.vallegrande.fideinexus.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.dto.PeopleInfo;

@Component
public class PeopleClient {

    private final WebClient webClient;

    public PeopleClient(WebClient.Builder builder, @Value("${external.services.people.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public Mono<PeopleInfo> findById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(PeopleInfo.class);
    }
}
