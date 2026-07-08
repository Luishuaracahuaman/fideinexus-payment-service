package vallegrande.edu.pe.paymentService.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.dto.BookSaleInfo;

@Component
public class BookClient {

    private final WebClient webClient;

    public BookClient(WebClient.Builder builder, @Value("${external.services.books.url}") String url) {
        this.webClient = builder.baseUrl(url).build();
    }

    public Mono<BookSaleInfo> findById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(BookSaleInfo.class);
    }

    public Mono<Void> decreaseStock(Long id, Integer quantity) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/{id}/decrease-stock")
                        .queryParam("quantity", quantity)
                        .build(id))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> increaseStock(Long id, Integer quantity) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/{id}/increase-stock")
                        .queryParam("quantity", quantity)
                        .build(id))
                .retrieve()
                .bodyToMono(Void.class);
    }
}
