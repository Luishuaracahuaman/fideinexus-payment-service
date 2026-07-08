package vallegrande.edu.pe.paymentService.dto;

import lombok.Data;

@Data
public class RequestInfo {
    private Long id;
    private String status;
    private String type;
    // Add other fields if needed for UI display
}
