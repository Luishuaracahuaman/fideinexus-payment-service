package pe.edu.vallegrande.fideinexus.payment.domain.exception;

/**
 * Excepción de dominio lanzada cuando un recurso solicitado no existe.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
