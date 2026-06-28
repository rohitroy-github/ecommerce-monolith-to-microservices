package springdev.ecomv1.orderservice.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardResponse {

    private final Long totalOrders;
    private final Long totalCustomers;
    private final Long totalSellers;
    private final Long totalProducts;
    private final List<AdminSellerOverviewResponse> sellers;
}