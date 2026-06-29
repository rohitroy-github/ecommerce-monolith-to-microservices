package springdev.ecomv1.dataseeder.dto.order;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentClientResponse {

	private Long paymentId;
	private Long orderId;
	private BigDecimal amount;
	private String status;
	private String transactionId;
}