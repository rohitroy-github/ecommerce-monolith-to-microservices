package springdev.ecomv1.dataseeder.dto.product;

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
public class ProductAvailabilityResponse {

	private Long productId;

	private Integer quantity;

	private boolean available;
}