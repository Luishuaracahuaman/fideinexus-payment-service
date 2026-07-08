package vallegrande.edu.pe.paymentService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import vallegrande.edu.pe.paymentService.dto.BookSaleInfo;
import vallegrande.edu.pe.paymentService.dto.PeopleInfo;
import vallegrande.edu.pe.paymentService.dto.RequestInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table("payments")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payment {

    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    @Column("people_id")
    private Long peopleId;

    @Column("request_id")
    private Long requestId;

    private BigDecimal amount;

    @Column("payment_method")
    private String paymentMethod;

    private String reference;

    @Column("payment_date")
    private LocalDateTime paymentDate;

    // Columna en BD (texto JSON plano) - Ignorado en la respuesta JSON
    @Column("items")
    @JsonIgnore
    private String itemsDb;

    // Lista real que recibe/envía al frontend
    @Transient
    private List<BookItem> items;

    private String status;

    @Column("cancel_reason")
    private String cancelReason;

    @Column("confirmed_by")
    private Long confirmedBy;

    @Column("confirmed_at")
    private LocalDateTime confirmedAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // ── Transient: datos enriquecidos ────────────────────────────────────────

    @Transient
    private RequestInfo request;

    @Transient
    private PeopleInfo people;

    // ── Clase Interna para los Items ─────────────────────────────────────────

    @Data
    public static class BookItem {
        private Long bookId;
        private Integer quantity;
        private BookSaleInfo book; // Datos enriquecidos desde el MS de Books
    }
}
