package pe.edu.vallegrande.fideinexus.payment.domain.model;

import pe.edu.vallegrande.fideinexus.payment.domain.vo.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo de Dominio puro (POJO).
 * 
 * Regla de Arquitectura Hexagonal:
 * - NO contiene anotaciones de Spring, JPA, R2DBC ni Jackson.
 * - Es completamente independiente de la infraestructura y el framework.
 * 
 * Representa el ciclo de vida del Pago centralizador/validador:
 * - Los pagos nacen en otros microservicios con monto, personaId y requestReferenceId (estado PENDING).
 * - Desde el frontend de finanzas se completan asignando metodoPago y cambiando el estado a COMPLETED.
 */
public class Payment {

    private Long id;
    private Long personaId;
    private Long requestReferenceId;
    private BigDecimal monto;
    private String metodoPago;
    private PaymentStatus status;
    private LocalDateTime validationDate;

    // Atributos de contexto y auditoría complementarios
    private Long tenantId;
    private String reference;
    private String cancelReason;
    private Long confirmedBy;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BookItem> items;

    // ── Sub-entidad de Dominio: Items de libros asociados ────────────────────

    public static class BookItem {
        private Long bookId;
        private Integer quantity;

        public BookItem() {}

        public BookItem(Long bookId, Integer quantity) {
            this.bookId = bookId;
            this.quantity = quantity;
        }

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // ── Constructores ────────────────────────────────────────────────────────

    public Payment() {}

    public Payment(Long id, Long personaId, Long requestReferenceId, BigDecimal monto,
                   String metodoPago, PaymentStatus status, LocalDateTime validationDate) {
        this.id = id;
        this.personaId = personaId;
        this.requestReferenceId = requestReferenceId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.status = status;
        this.validationDate = validationDate;
    }

    // ── Getters y Setters Principales del Dominio ─────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public Long getRequestReferenceId() {
        return requestReferenceId;
    }

    public void setRequestReferenceId(Long requestReferenceId) {
        this.requestReferenceId = requestReferenceId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(LocalDateTime validationDate) {
        this.validationDate = validationDate;
    }

    // ── Atributos complementarios ───────────────────────────────────────────

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Long getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(Long confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<BookItem> getItems() {
        return items;
    }

    public void setItems(List<BookItem> items) {
        this.items = items;
    }

    // ── Métodos puente (compatibilidad con servicios auxiliares) ────────────

    public Long getPeopleId() { return personaId; }
    public void setPeopleId(Long peopleId) { this.personaId = peopleId; }

    public Long getRequestId() { return requestReferenceId; }
    public void setRequestId(Long requestId) { this.requestReferenceId = requestId; }

    public BigDecimal getAmount() { return monto; }
    public void setAmount(BigDecimal amount) { this.monto = amount; }

    public String getPaymentMethod() { return metodoPago; }
    public void setPaymentMethod(String paymentMethod) { this.metodoPago = paymentMethod; }

    public LocalDateTime getPaymentDate() { return validationDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.validationDate = paymentDate; }
}
