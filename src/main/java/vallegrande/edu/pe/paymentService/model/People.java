package vallegrande.edu.pe.paymentService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("people")
public class People {
    
    @Id
    private Long id;

    @Column("tenant_id")
    private Long tenantId;

    private String estado;
    
    // Puedes mapear más columnas de tu tabla people si las necesitas para el frontend
    // private String nombres;
    // private String apellidos;
}
