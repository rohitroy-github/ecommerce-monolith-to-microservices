package springdev.ecomv1.dataseeder.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class ProductResponse {

	private Long id;

	private String name;

	private String description;

	private BigDecimal price;

	private Long sellerId;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}