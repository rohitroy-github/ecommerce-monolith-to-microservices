package springdev.ecomv1.orderservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Aggregates seller-level commerce metrics for reporting endpoints. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerMetricsResponse {

    private Long sellerId;

    private Integer totalProducts;

    private Integer totalOrders;

    private BigDecimal totalRevenue;
}