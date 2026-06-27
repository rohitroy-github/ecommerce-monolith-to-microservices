package springdev.ecomv1.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Carries customer and product details required to create a new order. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;
}
