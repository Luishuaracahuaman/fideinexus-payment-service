package pe.edu.vallegrande.fideinexus.payment.application.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeopleInfo {

    @JsonAlias({"peopleId", "personaId", "id"})
    private Long id;

    @JsonAlias({"nombres", "firstName", "first_name"})
    private String name;

    @JsonAlias({"apellidos", "lastName", "last_name"})
    private String lastName;

    @JsonAlias({"numeroDocumento", "dni", "document_number", "documentNumber"})
    private String documentNumber;

    @JsonAlias({"correo", "mail"})
    private String email;

    @JsonAlias({"telefono", "celular", "phone_number"})
    private String phone;

    @JsonAlias({"tenant_id"})
    private Long tenantId;

    public String getFullName() {
        String n = name != null ? name.trim() : "";
        String ln = lastName != null ? lastName.trim() : "";
        if (!n.isEmpty() && !ln.isEmpty()) return n + " " + ln;
        if (!n.isEmpty()) return n;
        return ln;
    }
}
