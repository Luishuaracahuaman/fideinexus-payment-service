package vallegrande.edu.pe.paymentService.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class BookDto {
    private Integer id;
    private Integer stock;
    private String nombre;
    private BigDecimal precio;
    private String nivel;
    private Integer grado;
    private LocalDate fechaIngreso;
    private String status;
}
