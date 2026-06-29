package springdev.ecomv1.dataseeder.dto.order;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductClientResponse {

	private Long id;
	private String name;
	private BigDecimal price;
	private Long sellerId;
}