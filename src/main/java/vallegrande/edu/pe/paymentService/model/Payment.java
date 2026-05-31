package vallegrande.edu.pe.paymentservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table("payment")
public class Payment {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    @Column("people_id")
    private Long peopleId;

    private BigDecimal monto;

    @Column("fecha_pago")
    private LocalDate fechaPago;

    private String estado;

    @Column("created_at")
    private LocalDateTime createdAt;

    private String referencia;

    @Column("reason_id")
    private Integer reasonId;

    @Column("payment_method_id")
    private Integer paymentMethodId;

    // ── Relaciones enriquecidas (no se persisten en la tabla) ──────────────
    @Transient
    private Reason reason;

    @Transient
    private PaymentMethod paymentMethod;
}
