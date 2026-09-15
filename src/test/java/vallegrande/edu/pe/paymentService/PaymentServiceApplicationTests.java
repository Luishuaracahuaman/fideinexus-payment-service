package vallegrande.edu.pe.paymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vallegrande.edu.pe.paymentService.client.BookClient;
import vallegrande.edu.pe.paymentService.client.PeopleClient;
import vallegrande.edu.pe.paymentService.client.RequestClient;
import vallegrande.edu.pe.paymentService.model.Payment;
import vallegrande.edu.pe.paymentService.repository.PaymentRepository;
import vallegrande.edu.pe.paymentService.service.impl.PaymentServiceImpl;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceApplicationTests {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private BookClient bookClient;

	@Mock
	private RequestClient requestClient;

	@Mock
	private PeopleClient peopleClient;

	private PaymentServiceImpl paymentService;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentServiceImpl(paymentRepository, bookClient, requestClient, peopleClient);
	}

	@ParameterizedTest
	@ValueSource(strings = { "CONFIRMADO", "ANULADO", "RECHAZADO", "REEMBOLSADO" })
	@DisplayName("Validar fallo al confirmar un pago si su estado no es POR CONFIRMAR")
	void testConfirmarPagoEstadosInvalidos(String estadoInvalido) {
		Payment payment = new Payment();
		payment.setId(1L);
		payment.setStatus(estadoInvalido);

		when(paymentRepository.findById(anyLong())).thenReturn(Mono.just(payment));

		Mono<Payment> result = paymentService.confirm(1L, 2L);

		StepVerifier.create(result)
				.expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
						throwable.getMessage().equals("El pago no está en estado POR CONFIRMAR"))
				.verify();
	}
}