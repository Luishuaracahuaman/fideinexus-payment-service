package pe.edu.vallegrande.fideinexus.payment.dto;

import lombok.Data;

@Data
public class PeopleInfo {
    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String estado;
    private Long tenantId;
}
