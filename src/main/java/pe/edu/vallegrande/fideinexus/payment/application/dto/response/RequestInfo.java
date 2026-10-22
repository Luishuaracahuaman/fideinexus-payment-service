package pe.edu.vallegrande.fideinexus.payment.application.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestInfo {
    private Long id;
    private String sacrament;
    private String status;
}
