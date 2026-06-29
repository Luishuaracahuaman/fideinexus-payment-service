package vallegrande.edu.pe.paymentService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("reason")
public class Reason {

    @Id
    private Integer id;

    private String nombre;

    private String descripcion;

    private Long requestId;
}
