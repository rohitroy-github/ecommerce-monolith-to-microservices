package springdev.ecomv1.dataseeder.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAvailabilityResponse {

	private Long productId;
	private Integer quantity;
	private boolean available;
}