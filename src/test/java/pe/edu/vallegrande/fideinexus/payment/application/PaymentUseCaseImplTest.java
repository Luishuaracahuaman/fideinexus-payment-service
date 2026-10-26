package pe.edu.vallegrande.fideinexus.payment.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.fideinexus.payment.domain.exception.ResourceNotFoundException;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.out.PaymentRepositoryPort;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentUseCaseImplTest {

    @Mock
    private PaymentRepositoryPort paymentRepositoryPort;

    @InjectMocks
    private PaymentUseCaseImpl paymentUseCase;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        samplePayment = new Payment();
        samplePayment.setId(1L);
        samplePayment.setPersonaId(100L);
        samplePayment.setRequestReferenceId(500L);
        samplePayment.setMonto(new BigDecimal("150.00"));
        samplePayment.setMetodoPago("YAPE");
        samplePayment.setStatus(PaymentStatus.PENDING);
        samplePayment.setTenantId(1L);
        samplePayment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debe registrar y persistir un pago inicial exitosamente en estado PENDING")
    void shouldCreatePaymentSuccessfully() {
        when(paymentRepositoryPort.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(10L);
            return Mono.just(p);
        });

        StepVerifier.create(paymentUseCase.createPayment(samplePayment))
                .assertNext(created -> {
                    org.junit.jupiter.api.Assertions.assertNotNull(created.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(PaymentStatus.PENDING, created.getStatus());
                    org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("150.00"), created.getMonto());
                    org.junit.jupiter.api.Assertions.assertEquals("YAPE", created.getMetodoPago());
                })
                .verifyComplete();

        verify(paymentRepositoryPort, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el monto del pago es nulo o menor/igual a cero")
    void shouldThrowExceptionWhenAmountIsInvalid() {
        samplePayment.setMonto(BigDecimal.ZERO);

        StepVerifier.create(paymentUseCase.createPayment(samplePayment))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException
                        && throwable.getMessage().contains("El monto del pago debe ser mayor a cero"))
                .verify();

        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe completar un pago en estado PENDING asignando método, confirmedBy y estado COMPLETED")
    void shouldCompletePaymentSuccessfully() {
        when(paymentRepositoryPort.findById(1L)).thenReturn(Mono.just(samplePayment));
        when(paymentRepositoryPort.save(any(Payment.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(paymentUseCase.completePayment(1L, "TRANSFERENCIA", "VOUCHER-9988", 200L))
                .assertNext(completed -> {
                    org.junit.jupiter.api.Assertions.assertEquals(PaymentStatus.COMPLETED, completed.getStatus());
                    org.junit.jupiter.api.Assertions.assertEquals("TRANSFERENCIA", completed.getMetodoPago());
                    org.junit.jupiter.api.Assertions.assertEquals("VOUCHER-9988", completed.getReference());
                    org.junit.jupiter.api.Assertions.assertEquals(200L, completed.getConfirmedBy());
                    org.junit.jupiter.api.Assertions.assertNotNull(completed.getValidationDate());
                })
                .verifyComplete();

        verify(paymentRepositoryPort, times(1)).findById(1L);
        verify(paymentRepositoryPort, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe fallar al completar un pago si este no existe en el repositorio")
    void shouldFailWhenPaymentNotFoundOnComplete() {
        when(paymentRepositoryPort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(paymentUseCase.completePayment(99L, "YAPE", "V-123", 1L))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(paymentRepositoryPort, times(1)).findById(99L);
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe fallar al completar si el pago ya se encuentra en estado COMPLETED")
    void shouldFailWhenPaymentIsAlreadyCompleted() {
        samplePayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepositoryPort.findById(1L)).thenReturn(Mono.just(samplePayment));

        StepVerifier.create(paymentUseCase.completePayment(1L, "YAPE", "V-123", 1L))
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException
                        && throwable.getMessage().contains("Solo es posible completar pagos en estado PENDING"))
                .verify();

        verify(paymentRepositoryPort, times(1)).findById(1L);
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }
}
