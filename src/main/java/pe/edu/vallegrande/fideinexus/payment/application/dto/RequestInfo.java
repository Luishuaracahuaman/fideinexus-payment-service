package pe.edu.vallegrande.fideinexus.payment.application.dto;

import lombok.Data;

@Data
public class RequestInfo {
    private Long id;
    private String status;
    private String type;
}
