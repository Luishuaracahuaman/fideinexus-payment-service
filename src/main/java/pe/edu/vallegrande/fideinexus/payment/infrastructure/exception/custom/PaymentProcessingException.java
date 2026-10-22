package pe.edu.vallegrande.fideinexus.payment.infrastructure.exception.custom;

/**
 * Excepción personalizada para errores en el procesamiento de pagos.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/exception/custom/)
 */
public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
