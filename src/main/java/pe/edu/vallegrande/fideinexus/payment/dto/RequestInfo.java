package pe.edu.vallegrande.fideinexus.payment.dto;

import lombok.Data;

@Data
public class RequestInfo {
    private Long id;
    private String status;
    private String type;
}
