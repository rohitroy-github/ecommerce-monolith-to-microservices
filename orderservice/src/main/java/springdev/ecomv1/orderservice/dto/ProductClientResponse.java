package springdev.ecomv1.orderservice.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductClientResponse {

    private Long id;

    private String name;

    private BigDecimal price;
}
