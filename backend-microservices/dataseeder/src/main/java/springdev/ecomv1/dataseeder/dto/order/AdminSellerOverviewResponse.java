package springdev.ecomv1.dataseeder.dto.order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminSellerOverviewResponse {

	private final Long sellerId;
	private final String sellerName;
	private final String sellerEmail;
	private final Long totalOrders;
	private final Long totalProducts;
}