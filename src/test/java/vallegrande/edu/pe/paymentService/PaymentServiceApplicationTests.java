package vallegrande.edu.pe.paymentService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.repository.PaymentRepository;
import vallegrande.edu.pe.paymentService.service.impl.PaymentServiceImpl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceApplicationTests {

	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@ParameterizedTest
	@ValueSource(strings = { "CONFIRMADO", "ANULADO", "RECHAZADO", "REEMBOLSADO" })
	@DisplayName("Validar fallo al confirmar un pago si su estado no es POR CONFIRMAR")
	void testConfirmarPagoEstadosInvalidos(String estadoInvalido) {
		// Arrange: Preparamos un objeto Payment simulando un estado inválido
		Payment payment = new Payment();
		payment.setId(1L);
		payment.setStatus(estadoInvalido);

		// Simulamos (Mock) la respuesta del repositorio
		when(paymentRepository.findById(anyLong())).thenReturn(Mono.just(payment));

		// Act: Intentamos confirmar el pago a través del servicio
		Mono<Payment> result = paymentService.confirm(1L, 2L);

		// Assert: Verificamos de forma reactiva que lance la excepción esperada
		StepVerifier.create(result)
				.expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
						throwable.getMessage().equals("El pago no está en estado POR CONFIRMAR"))
				.verify();
	}
}