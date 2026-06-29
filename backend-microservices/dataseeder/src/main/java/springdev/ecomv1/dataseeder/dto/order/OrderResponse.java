package springdev.ecomv1.dataseeder.dto.order;

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
public class OrderResponse {

	private String message;
	private Long customerId;
	private Long productId;
	private Integer quantity;
	private String status;
}