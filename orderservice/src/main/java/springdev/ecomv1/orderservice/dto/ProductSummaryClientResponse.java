package springdev.ecomv1.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

/** Holds compact product fields used in lightweight cross-service responses. */
@Getter
@Setter
public class ProductSummaryClientResponse {

    private Long id;

    private String name;
}
