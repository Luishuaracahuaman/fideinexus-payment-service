package vallegrande.edu.pe.paymentService.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payment_types")
@Data
public class PaymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}