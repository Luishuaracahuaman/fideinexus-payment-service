package vallegrande.edu.pe.paymentService.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reasons")
public class Reason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "request_id")
    private Integer requestId;
}