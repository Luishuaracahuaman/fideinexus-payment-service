package pe.edu.vallegrande.fideinexus.payment.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeopleInfo {
    private Long id;

    @JsonAlias({"name", "given_name", "first_name"})
    private String nombre;

    @JsonAlias({"lastname", "last_name", "family_name"})
    private String apellido;

    @JsonAlias({"dni", "numberDocument", "documentNumber", "identification"})
    private String dni;

    @JsonAlias({"status", "state"})
    private String estado;

    @JsonAlias({"tenant_id", "parroquiaId"})
    private Long tenantId;

    private String email;
    private String phone;
    private String role;

    public String getName() {
        return nombre;
    }

    public void setName(String name) {
        if (this.nombre == null) {
            this.nombre = name;
        }
    }

    public String getLastname() {
        return apellido;
    }

    public void setLastname(String lastname) {
        if (this.apellido == null) {
            this.apellido = lastname;
        }
    }

    public String getStatus() {
        return estado;
    }

    public void setStatus(String status) {
        if (this.estado == null) {
            this.estado = status;
        }
    }
}
