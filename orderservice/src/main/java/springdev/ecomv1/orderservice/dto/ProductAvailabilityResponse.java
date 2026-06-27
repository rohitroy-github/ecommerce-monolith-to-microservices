package springdev.ecomv1.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

/** Returns stock availability details from product-service checks. */
@Getter
@Setter
public class ProductAvailabilityResponse {

    private Long productId;

    private Integer quantity;

    private boolean available;
}
