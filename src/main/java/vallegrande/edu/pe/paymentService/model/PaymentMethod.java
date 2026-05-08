package vallegrande.edu.pe.paymentService.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payment_methods")
@Data
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1)
    private String nombre; // 'E', 'T', 'Y', 'P'

    @Column(length = 255)
    private String descripcion;
}