package vallegrande.edu.pe.paymentService.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.paymentService.domain.model.Payment;
import vallegrande.edu.pe.paymentService.domain.port.in.CompletePaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.in.CreatePaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.in.QueryPaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.in.UpdatePaymentUseCase;
import vallegrande.edu.pe.paymentService.domain.port.out.PaymentRepositoryPort;
import vallegrande.edu.pe.paymentService.domain.vo.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Capa de APLICACIÓN: Implementación de los Casos de Uso.
 *
 * Contiene la lógica de negocio pura y la orquestación:
 * 1. Centralización de pagos nacidos en otros microservicios con monto, personaId y requestReferenceId.
 *    Inicialmente se persisten en estado PENDING.
 * 2. Finalización y validación desde el frontend de Finanzas asignando el método de pago (EFECTIVO, etc.)
 *    y transicionando el estado a COMPLETED con su validationDate.
 * 3. Inversión de dependencias: se comunica con el exterior únicamente mediante puertos (PaymentRepositoryPort).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentUseCaseImpl implements CreatePaymentUseCase, CompletePaymentUseCase, QueryPaymentUseCase, UpdatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;

    // ─────────────────────────────────────────────────────────────────────────
    // CASO DE USO 1: Crear pago inicial (Centralizador)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public Mono<Payment> createPayment(Payment payment) {
        return Mono.fromCallable(() -> {
            if (payment == null) {
                throw new IllegalArgumentException("Los datos del pago no pueden ser nulos.");
            }
            if (payment.getMonto() == null || payment.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
            }
            if (payment.getPersonaId() == null) {
                throw new IllegalArgumentException("El identificador de la persona (personaId) es obligatorio.");
            }

            // Regla de origen: el pago proviene de una solicitud o de venta de libros
            validatePaymentOrigin(payment);

            // Regla de negocio: Todo pago nuevo nace en estado PENDING y sin método de pago asignado
            payment.setStatus(PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            return payment;
        })
        .flatMap(paymentRepositoryPort::save)
        .doOnSuccess(saved -> log.info("[PAYMENT] Pago creado exitosamente con ID={}, status={}", saved.getId(), saved.getStatus()))
        .doOnError(err -> log.error("[PAYMENT] Error al registrar pago inicial: {}", err.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO DE USO 2: Completar pago (Frontend de Finanzas)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public Mono<Payment> completePayment(Long id, String metodoPago) {
        return completePayment(id, metodoPago, null, null);
    }

    @Override
    public Mono<Payment> completePayment(Long id, String metodoPago, String reference, Long confirmedBy) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El método de pago es obligatorio para completar la operación."));
        }

        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el pago con ID: " + id)))
                .flatMap(payment -> {
                    // Regla de negocio: Solo se puede completar un pago que se encuentre en estado PENDING
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "Solo es posible completar pagos en estado PENDING. Estado actual: " + payment.getStatus()));
                    }

                    payment.setMetodoPago(metodoPago.trim().toUpperCase());
                    payment.setReference(reference);
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setValidationDate(LocalDateTime.now());
                    payment.setConfirmedBy(confirmedBy);
                    payment.setConfirmedAt(LocalDateTime.now());
                    payment.setUpdatedAt(LocalDateTime.now());

                    return paymentRepositoryPort.save(payment);
                })
                .doOnSuccess(completed -> log.info("[PAYMENT] Pago con ID={} completado con éxito con método={}", id, metodoPago))
                .doOnError(err -> log.warn("[PAYMENT] No se pudo completar el pago con ID={}: {}", id, err.getMessage()));
    }

    @Override
    public Mono<Payment> cancelPayment(Long id, String reason) {
        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el pago con ID: " + id)))
                .flatMap(payment -> {
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "Solo se puede cancelar un pago en estado PENDING. Estado actual: " + payment.getStatus()));
                    }
                    payment.setStatus(PaymentStatus.CANCELLED);
                    payment.setCancelReason(reason);
                    payment.setUpdatedAt(LocalDateTime.now());
                    return paymentRepositoryPort.save(payment);
                });
    }

    @Override
    public Mono<Payment> rejectPayment(Long id, String reason) {
        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el pago con ID: " + id)))
                .flatMap(payment -> {
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "Solo se puede rechazar un pago en estado PENDING. Estado actual: " + payment.getStatus()));
                    }
                    payment.setStatus(PaymentStatus.REJECTED);
                    payment.setCancelReason(reason);
                    payment.setUpdatedAt(LocalDateTime.now());
                    return paymentRepositoryPort.save(payment);
                });
    }

    @Override
    public Mono<Payment> refundPayment(Long id, String reason) {
        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el pago con ID: " + id)))
                .flatMap(payment -> {
                    if (payment.getStatus() != PaymentStatus.COMPLETED) {
                        return Mono.error(new IllegalStateException(
                                "Solo se puede reembolsar un pago en estado COMPLETED. Estado actual: " + payment.getStatus()));
                    }
                    payment.setStatus(PaymentStatus.REFUNDED);
                    payment.setCancelReason(reason);
                    payment.setUpdatedAt(LocalDateTime.now());
                    return paymentRepositoryPort.save(payment);
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO DE USO 3: Consultas Reactivas
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public Flux<Payment> findAll() {
        return paymentRepositoryPort.findAll();
    }

    @Override
    public Mono<Payment> findById(Long id) {
        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Pago no encontrado con id=" + id)));
    }

    @Override
    public Flux<Payment> findByTenantId(Long tenantId) {
        return paymentRepositoryPort.findByTenantId(tenantId);
    }

    @Override
    public Flux<Payment> findByPeopleId(Long peopleId) {
        return paymentRepositoryPort.findByPersonaId(peopleId);
    }

    @Override
    public Flux<Payment> findByStatus(String status) {
        PaymentStatus paymentStatus = PaymentStatus.fromString(status);
        return paymentRepositoryPort.findByStatus(paymentStatus);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CASO DE USO 4: Edición segura de datos de formulario y estado
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public Mono<Payment> updatePayment(Long id, Payment updates) {
        if (updates == null) {
            return Mono.error(new IllegalArgumentException("Los datos de actualización no pueden ser nulos."));
        }

        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró el pago con ID: " + id)))
                .flatMap(existing -> {
                    // 1. Modificar únicamente los datos editados en el formulario (no nulos)
                    if (updates.getMonto() != null) {
                        if (updates.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                            return Mono.error(new IllegalArgumentException("El monto del pago debe ser mayor a cero."));
                        }
                        existing.setMonto(updates.getMonto());
                    }

                    if (updates.getPersonaId() != null) {
                        existing.setPersonaId(updates.getPersonaId());
                    }

                    if (updates.getRequestReferenceId() != null) {
                        existing.setRequestReferenceId(updates.getRequestReferenceId());
                    }

                    if (updates.getMetodoPago() != null && !updates.getMetodoPago().isBlank()) {
                        existing.setMetodoPago(updates.getMetodoPago().trim().toUpperCase());
                    }

                    if (updates.getReference() != null) {
                        existing.setReference(updates.getReference());
                    }

                    if (updates.getTenantId() != null) {
                        existing.setTenantId(updates.getTenantId());
                    }

                    if (updates.getCancelReason() != null) {
                        existing.setCancelReason(updates.getCancelReason());
                    }

                    if (updates.getConfirmedBy() != null) {
                        existing.setConfirmedBy(updates.getConfirmedBy());
                    }

                    if (updates.getItems() != null && !updates.getItems().isEmpty()) {
                        existing.setItems(updates.getItems());
                    }

                    // 2. Modificar el estado si fue enviado en el formulario
                    if (updates.getStatus() != null) {
                        existing.setStatus(updates.getStatus());

                        if (updates.getStatus() == PaymentStatus.COMPLETED) {
                            if (existing.getValidationDate() == null) {
                                existing.setValidationDate(LocalDateTime.now());
                            }
                            if (existing.getConfirmedAt() == null) {
                                existing.setConfirmedAt(LocalDateTime.now());
                            }
                        }
                    }

                    existing.setUpdatedAt(LocalDateTime.now());

                    return paymentRepositoryPort.save(existing);
                })
                .doOnSuccess(saved -> log.info("[PAYMENT] Pago con ID={} actualizado exitosamente. status={}", id, saved.getStatus()))
                .doOnError(err -> log.warn("[PAYMENT] Error al actualizar pago con ID={}: {}", id, err.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validaciones auxiliares
    // ─────────────────────────────────────────────────────────────────────────

    private void validatePaymentOrigin(Payment payment) {
        boolean hasRequest = payment.getRequestReferenceId() != null;
        boolean hasItems = payment.getItems() != null && !payment.getItems().isEmpty();

        if (hasRequest && hasItems) {
            throw new IllegalArgumentException(
                    "El pago no puede referenciar simultáneamente una solicitud (requestReferenceId) y una venta de libros.");
        }
        if (!hasRequest && !hasItems) {
            throw new IllegalArgumentException(
                    "El pago debe estar asociado al menos a una solicitud de origen o a una lista de libros.");
        }
    }
}
