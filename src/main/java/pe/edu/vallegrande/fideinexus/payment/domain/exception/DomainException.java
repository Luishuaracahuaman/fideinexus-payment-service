package pe.edu.vallegrande.fideinexus.payment.domain.exception;

/**
 * Excepción base de dominio (abstracta).
 *
 * Representa cualquier error de lógica o invariante del Dominio.
 * Extiende RuntimeException para integrarse limpiamente con el flujo reactivo.
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
