package pe.edu.vallegrande.fideinexus.payment.infrastructure.exception.custom;

/**
 * Excepción personalizada para fallos en la integración con servicios externos.
 * 
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/exception/custom/)
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
