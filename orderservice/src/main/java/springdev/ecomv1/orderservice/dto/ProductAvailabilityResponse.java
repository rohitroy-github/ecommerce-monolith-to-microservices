package springdev.ecomv1.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAvailabilityResponse {

    private Long productId;

    private Integer quantity;

    private boolean available;
}
