package springdev.ecomv1.orderservice.dashboard;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import springdev.ecomv1.orderservice.client.ProductClient;
import springdev.ecomv1.orderservice.client.UserClient;
import springdev.ecomv1.orderservice.dto.AdminDashboardResponse;
import springdev.ecomv1.orderservice.dto.AdminSellerOverviewResponse;
import springdev.ecomv1.orderservice.dto.ProductSummaryClientResponse;
import springdev.ecomv1.orderservice.dto.ProductClientResponse;
import springdev.ecomv1.orderservice.dto.SellerMetricsResponse;
import springdev.ecomv1.orderservice.dto.SellerOrderResponse;
import springdev.ecomv1.orderservice.dto.UserClientResponse;
import springdev.ecomv1.orderservice.exception.BadGatewayException;
import springdev.ecomv1.orderservice.exception.ResourceNotFoundException;
import springdev.ecomv1.orderservice.exception.ServiceUnavailableException;
import springdev.ecomv1.orderservice.repository.OrderItemRepository;
import springdev.ecomv1.orderservice.repository.OrderRepository;
import springdev.ecomv1.orderservice.service.OrderService;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductClient productClient;
	private final UserClient userClient;

	@Transactional(readOnly = true)
	public AdminDashboardResponse getAdminDashboard() {
		List<UserClientResponse> users = getAllUsers();
		List<ProductClientResponse> products = getAllProducts();

		Map<Long, Long> sellerOrderCounts = orderItemRepository.findAll().stream()
				.filter(item -> item.getSellerId() != null && item.getOrder() != null && item.getOrder().getId() != null)
				.collect(Collectors.groupingBy(
						item -> item.getSellerId(),
						Collectors.mapping(item -> item.getOrder().getId(),
								Collectors.collectingAndThen(Collectors.toSet(), orderIds -> (long) orderIds.size()))));

		Map<Long, Long> sellerProductCounts = products.stream()
				.filter(product -> product.getSellerId() != null)
				.collect(Collectors.groupingBy(ProductClientResponse::getSellerId, Collectors.counting()));

		List<AdminSellerOverviewResponse> sellers = users.stream()
				.filter(user -> "SELLER".equalsIgnoreCase(user.getRole()))
				.map(user -> AdminSellerOverviewResponse.builder()
						.sellerId(user.getId())
						.sellerName(user.getName())
						.sellerEmail(user.getEmail())
						.totalOrders(sellerOrderCounts.getOrDefault(user.getId(), 0L))
						.totalProducts(sellerProductCounts.getOrDefault(user.getId(), 0L))
						.build())
				.sorted((left, right) -> Long.compare(
						right.getTotalOrders() == null ? 0L : right.getTotalOrders(),
						left.getTotalOrders() == null ? 0L : left.getTotalOrders()))
				.toList();

		long totalCustomers = users.stream()
				.filter(user -> "CUSTOMER".equalsIgnoreCase(user.getRole()))
				.count();

		long totalSellers = sellers.size();
		long totalProducts = products.size();
		long totalOrders = orderRepository.count();

		return AdminDashboardResponse.builder()
				.totalOrders(totalOrders)
				.totalCustomers(totalCustomers)
				.totalSellers(totalSellers)
				.totalProducts(totalProducts)
				.sellers(sellers)
				.build();
	}

	/**
	 * Workflow summary:
	 * 1) Validate seller exists in user-service (backed by user DB).
	 * 2) Load seller-scoped orders from order-service business method.
	 * 3) Fetch seller product list from product-service.
	 * 4) Aggregate total revenue from order totals.
	 * 5) Return a compact dashboard metrics payload.
	 */
	@Transactional(readOnly = true)
	public SellerMetricsResponse getSellerMetrics(Long sellerId) {
		// Step 1: Validate seller existence in user DB before computing dashboard metrics.
		validateSellerExists(sellerId);

		// Step 2: Reuse seller order retrieval flow to avoid duplicate query/mapping logic.
		List<SellerOrderResponse> sellerOrders = orderService.getOrdersBySellerId(sellerId);
		// Step 3: Pull seller product count from product-service (cross-service aggregation).
		List<ProductSummaryClientResponse> sellerProducts = productClient.getProductsBySellerId(sellerId);

		if (sellerOrders.isEmpty()) {
			return SellerMetricsResponse.builder()
					.sellerId(sellerId)
					.totalProducts(sellerProducts.size())
					.totalOrders(0)
					.totalRevenue(BigDecimal.ZERO)
					.build();
		}

		// Step 4: Aggregate seller revenue from order totals returned by seller-order route logic.
		BigDecimal totalRevenue = sellerOrders.stream()
				.map(order -> order.getTotalAmount() == null ? BigDecimal.ZERO : order.getTotalAmount())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Step 5: Build dashboard response consumed by seller/admin analytics views.
		return SellerMetricsResponse.builder()
				.sellerId(sellerId)
				.totalProducts(sellerProducts.size())
				.totalOrders(sellerOrders.size())
				.totalRevenue(totalRevenue)
				.build();
	}

	private void validateSellerExists(Long sellerId) {
		try {
			UserClientResponse seller = userClient.getUserById(sellerId);
			if (seller == null || seller.getRole() == null || !"SELLER".equalsIgnoreCase(seller.getRole())) {
				throw new ResourceNotFoundException("Seller not found with id: " + sellerId);
			}
		} catch (FeignException.NotFound ex) {
			throw new ResourceNotFoundException("Seller not found with id: " + sellerId);
		} catch (RetryableException ex) {
			throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
		} catch (FeignException ex) {
			throw new BadGatewayException("User service call failed");
		}
	}

	private List<UserClientResponse> getAllUsers() {
		try {
			List<UserClientResponse> users = userClient.getAllUsers();
			return users == null ? Collections.emptyList() : users;
		} catch (RetryableException ex) {
			throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
		} catch (FeignException ex) {
			throw new BadGatewayException("User service call failed");
		}
	}

	private List<ProductClientResponse> getAllProducts() {
		try {
			List<ProductClientResponse> products = productClient.getAllProducts();
			return products == null ? Collections.emptyList() : products;
		} catch (RetryableException ex) {
			throw new ServiceUnavailableException("Product service is currently unavailable. Please try again later.");
		} catch (FeignException ex) {
			throw new BadGatewayException("Product service call failed");
		}
	}
}
