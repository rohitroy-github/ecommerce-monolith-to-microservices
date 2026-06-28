package springdev.ecomv1.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Defines the quantity to deduct from inventory after order confirmation. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReduceStockRequest {

    private Integer quantity;
}
