package vallegrande.edu.pe.paymentservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("payment_method")
public class PaymentMethod {

    @Id
    private Integer id;

    private String nombre;

    private String descripcion;
}
