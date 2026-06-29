package springdev.ecomv1.dataseeder.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerOrderResponse {

	private Long orderId;
	private Long sellerId;
	private Long customerId;
	private Long productId;
	private Integer quantity;
	private String status;
	private BigDecimal totalAmount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}