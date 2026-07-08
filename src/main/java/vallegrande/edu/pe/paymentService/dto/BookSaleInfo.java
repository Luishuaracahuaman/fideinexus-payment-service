package vallegrande.edu.pe.paymentService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookSaleInfo {
    private Integer id;
    private String nombre; // o title
    private String nivel;
    private Integer grado;
    private Double precio; // o price
}
