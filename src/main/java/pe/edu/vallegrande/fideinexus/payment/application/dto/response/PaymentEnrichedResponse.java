package pe.edu.vallegrande.fideinexus.payment.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.fideinexus.payment.domain.model.Payment.BookItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentEnrichedResponse {
    private Long paymentId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String date;
    private LocalDateTime validationDate;
    private Long tenantId;
    private String reference;
    private CustomerInfo customer;
    private List<BookItem> items;
    private List<BookSaleInfo> booksEnriched;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomerInfo {
        private Long id;
        private String name;
        private String documentNumber;
        private String email;
        private String phone;
    }
}
