package vallegrande.edu.pe.paymentService.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.model.BookDto;

@Component
public class BookClient {

    private final WebClient webClient;

    public BookClient(WebClient.Builder builder, @Value("${external.services.books.url}") String booksUrl) {
        this.webClient = builder.baseUrl(booksUrl).build();
    }

    public Flux<BookDto> findAllActive() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(BookDto.class);
    }

    public Mono<BookDto> findById(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(BookDto.class);
    }

    public Flux<BookDto> findByNivel(String nivel) {
        return webClient.get()
                .uri("/nivel/{nivel}", nivel)
                .retrieve()
                .bodyToFlux(BookDto.class);
    }

    public Flux<BookDto> findByGradoAndNivel(Integer grado, String nivel) {
        return webClient.get()
                .uri("/grado/{grado}/nivel/{nivel}", grado, nivel)
                .retrieve()
                .bodyToFlux(BookDto.class);
    }
}
