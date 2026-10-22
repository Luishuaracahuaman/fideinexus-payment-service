package pe.edu.vallegrande.fideinexus.payment.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.exception.ResourceNotFoundException;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.CompletePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.CreatePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.QueryPaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.in.UpdatePaymentUseCase;
import pe.edu.vallegrande.fideinexus.payment.domain.port.out.PaymentRepositoryPort;
import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;

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

            // Regla de origen: el pago proviene de una solicitud/servicio o de venta de libros
            validatePaymentOrigin(payment);

            // Respetar y asignar método de pago si vino en la solicitud
            if (payment.getMetodoPago() != null && !payment.getMetodoPago().isBlank()) {
                payment.setMetodoPago(payment.getMetodoPago().trim().toUpperCase());
            }

            // Respetar y asignar referencia si vino en la solicitud
            if (payment.getReference() != null && !payment.getReference().isBlank()) {
                payment.setReference(payment.getReference().trim());
            }

            // Si el estado vino nulo, por defecto PENDING
            if (payment.getStatus() == null) {
                payment.setStatus(PaymentStatus.PENDING);
            }

            // Si el estado es COMPLETED y no tiene validationDate, asignar ahora
            if (payment.getStatus() == PaymentStatus.COMPLETED && payment.getValidationDate() == null) {
                payment.setValidationDate(LocalDateTime.now());
            }

            if (payment.getCreatedAt() == null) {
                payment.setCreatedAt(LocalDateTime.now());
            }
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
        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el pago con ID: " + id)))
                .flatMap(payment -> {
                    // Regla de negocio: Solo se puede completar un pago que se encuentre en estado PENDING
                    if (payment.getStatus() != PaymentStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "Solo es posible completar pagos en estado PENDING. Estado actual: " + payment.getStatus()));
                    }

                    // Si se envía un nuevo método de pago en el request, actualizarlo; de lo contrario mantener el existente
                    if (metodoPago != null && !metodoPago.trim().isEmpty()) {
                        payment.setMetodoPago(metodoPago.trim().toUpperCase());
                    }

                    // Validar sobre la entidad ya consolidada
                    if (payment.getMetodoPago() == null || payment.getMetodoPago().trim().isEmpty()) {
                        return Mono.error(new IllegalArgumentException("El método de pago es obligatorio para completar la operación."));
                    }

                    // Si se envía una nueva referencia, actualizarla; de lo contrario mantener la existente
                    if (reference != null && !reference.trim().isEmpty()) {
                        payment.setReference(reference.trim());
                    }

                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setValidationDate(LocalDateTime.now());
                    if (confirmedBy != null) {
                        payment.setConfirmedBy(confirmedBy);
                    }
                    payment.setConfirmedAt(LocalDateTime.now());
                    payment.setUpdatedAt(LocalDateTime.now());

                    return paymentRepositoryPort.save(payment);
                })
                .doOnSuccess(completed -> log.info("[PAYMENT] Pago con ID={} completado con éxito con método={}, confirmedBy={}", 
                        id, completed.getMetodoPago(), completed.getConfirmedBy()))
                .doOnError(err -> log.warn("[PAYMENT] No se pudo completar el pago con ID={}: {}", id, err.getMessage()));
    }

    @Override
    public Mono<Payment> cancelPayment(Long id, String reason) {
        return paymentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el pago con ID: " + id)))
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el pago con ID: " + id)))
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el pago con ID: " + id)))
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Pago no encontrado con id=" + id)));
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
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("No se encontró el pago con ID: " + id)))
                .flatMap(existing -> {
                    // Regla de negocio: Solo se pueden editar pagos en estado POR CONFIRMAR (PENDING)
                    if (existing.getStatus() != PaymentStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "No se puede editar el pago porque se encuentra en estado " + existing.getStatus() 
                                + ". Solo se pueden editar pagos en estado POR CONFIRMAR."));
                    }

                    // 1. Modificar items (Venta de productos) si fue enviado
                    if (updates.getItems() != null) {
                        if (updates.getItems().isEmpty()) {
                            return Mono.error(new IllegalArgumentException("La venta de productos debe contener al menos 1 producto."));
                        }
                        existing.setItems(updates.getItems());
                    }

                    // 2. Modificar monto si fue editado en el formulario
                    if (updates.getMonto() != null) {
                        // Para venta de libros con items el monto es el total de la compra (> 0); para trámites el mínimo es S/. 10
                        boolean hasItems = (existing.getItems() != null && !existing.getItems().isEmpty())
                                || (updates.getItems() != null && !updates.getItems().isEmpty());
                        if (hasItems) {
                            if (updates.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                                return Mono.error(new IllegalArgumentException("El monto total de la venta debe ser mayor a cero."));
                            }
                        } else {
                            if (updates.getMonto().compareTo(new BigDecimal("10")) < 0) {
                                return Mono.error(new IllegalArgumentException("El monto mínimo permitido es de S/. 10.00."));
                            }
                        }
                        existing.setMonto(updates.getMonto());
                    }

                    // 3. Feligrés (personaId) y Solicitud (requestReferenceId/serviceId) NO son editables según regla de negocio
                    // Se mantienen bloqueados con sus valores originales de base de datos.

                    // 4. Modificar método de pago si fue editado en el formulario
                    if (updates.getMetodoPago() != null && !updates.getMetodoPago().isBlank()) {
                        existing.setMetodoPago(updates.getMetodoPago().trim().toUpperCase());
                    }

                    // 5. Modificar referencia / voucher si fue editada en el formulario
                    if (updates.getReference() != null) {
                        existing.setReference(updates.getReference().trim());
                    }

                    if (updates.getCancelReason() != null) {
                        existing.setCancelReason(updates.getCancelReason());
                    }

                    if (updates.getConfirmedBy() != null) {
                        existing.setConfirmedBy(updates.getConfirmedBy());
                    }

                    // 6. Modificar estado si fue enviado
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
                .doOnSuccess(saved -> log.info("[PAYMENT] Pago con ID={} actualizado exitosamente. monto={}, method={}, ref={}", 
                        id, saved.getMonto(), saved.getMetodoPago(), saved.getReference()))
                .doOnError(err -> log.warn("[PAYMENT] Error al actualizar pago con ID={}: {}", id, err.getMessage()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validaciones auxiliares
    // ─────────────────────────────────────────────────────────────────────────

    private void validatePaymentOrigin(Payment payment) {
        boolean hasRequest = (payment.getRequestReferenceId() != null && payment.getRequestReferenceId() > 0) || (payment.getServiceId() != null && payment.getServiceId() > 0);
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
