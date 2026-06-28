package springdev.ecomv1.orderservice.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/** Captures payment-service response data consumed by the order workflow. */
@Getter
@Setter
public class PaymentClientResponse {

    private Long paymentId;

    private Long orderId;

    private BigDecimal amount;

    private String status;

    private String transactionId;
}
