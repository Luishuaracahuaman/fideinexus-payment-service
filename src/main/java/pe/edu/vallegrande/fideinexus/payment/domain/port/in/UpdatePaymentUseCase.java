package pe.edu.vallegrande.fideinexus.payment.domain.port.in;

import reactor.core.publisher.Mono;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment;

/**
 * Puerto de Entrada (Driving Port) para editar un pago existente.
 *
 * Expone la operación de actualización selectiva ("patch") para modificar
 * los campos provistos desde el formulario (monto, método de pago, referencia, estado, etc.)
 * sin alterar los datos no incluidos en la petición.
 */
public interface UpdatePaymentUseCase {

    /**
     * Actualiza de forma segura únicamente los campos provistos (no nulos) de un pago,
     * incluyendo el cambio de estado si fue solicitado.
     *
     * @param id Identificador único del pago a editar
     * @param paymentUpdates Objeto con los nuevos valores de los campos a actualizar
     * @return Mono con el pago actualizado y persistido
     */
    Mono<Payment> updatePayment(Long id, Payment paymentUpdates);
}
