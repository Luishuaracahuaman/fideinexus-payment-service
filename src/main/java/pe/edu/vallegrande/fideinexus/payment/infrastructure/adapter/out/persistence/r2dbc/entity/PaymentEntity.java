package pe.edu.vallegrande.fideinexus.payment.infrastructure.adapter.out.persistence.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de Persistencia Reactiva para Spring Data R2DBC.
 *
 * Ubicación: Capa de INFRAESTRUCTURA (infrastructure/adapter/out/persistence/r2dbc/entity/).
 * 
 * Regla de Arquitectura Hexagonal:
 * - El Dominio NUNCA conoce esta entidad ni sus anotaciones (@Table, @Id, @Column).
 * - Utiliza R2DBC (@Table de org.springframework.data.relational), evitando dependencias
 *   bloqueantes de Spring Data JPA / Hibernate.
 */
@Data
@Table("payments")
public class PaymentEntity {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    @Column("people_id")
    private Long personaId;

    @Column("request_id")
    private Long requestReferenceId;

    @Column("amount")
    private BigDecimal monto;

    @Column("payment_method")
    private String metodoPago;

    @Column("reference")
    private String reference;

    @Column("payment_date")
    private LocalDateTime validationDate;

    @Column("status")
    private String status;

    @Column("cancel_reason")
    private String cancelReason;

    @Column("confirmed_by")
    private Long confirmedBy;

    @Column("confirmed_at")
    private LocalDateTime confirmedAt;

    @Column("items")
    private String itemsJson;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
