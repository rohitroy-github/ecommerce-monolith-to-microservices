package springdev.ecomv1.dataseeder.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import springdev.ecomv1.dataseeder.dto.order.AdminDashboardResponse;
import springdev.ecomv1.dataseeder.dto.order.CreateOrderRequest;
import springdev.ecomv1.dataseeder.dto.order.OrderResponse;
import springdev.ecomv1.dataseeder.dto.order.OrderStatusResponse;
import springdev.ecomv1.dataseeder.dto.order.SellerMetricsResponse;
import springdev.ecomv1.dataseeder.dto.order.SellerOrderResponse;
import springdev.ecomv1.dataseeder.dto.order.UpdateOrderStatusRequest;

@Component
public class OrderClient {

	private final RestClient restClient;

	public OrderClient(@Qualifier("orderRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public RestClient restClient() {
		return restClient;
	}

	public OrderResponse createOrder(CreateOrderRequest request) {
		return restClient.post()
				.uri("/api/orders")
				.body(request)
				.retrieve()
				.body(OrderResponse.class);
	}

	public OrderResponse createOrder(CreateOrderRequest request, String jwtToken) {
		return restClient.post()
				.uri("/api/orders")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
				.body(request)
				.retrieve()
				.body(OrderResponse.class);
	}

	public OrderStatusResponse getOrderById(Long id) {
		return restClient.get()
				.uri("/api/orders/{id}", id)
				.retrieve()
				.body(OrderStatusResponse.class);
	}

	public List<SellerOrderResponse> getOrdersBySellerId(Long sellerId) {
		return restClient.get()
				.uri("/api/orders/sellers/{sellerId}", sellerId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<SellerOrderResponse>>() {
				});
	}

	public List<SellerOrderResponse> getOrdersBySellerId(Long sellerId, String jwtToken) {
		return restClient.get()
				.uri("/api/orders/sellers/{sellerId}", sellerId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
				.retrieve()
				.body(new ParameterizedTypeReference<List<SellerOrderResponse>>() {
				});
	}

	public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
		return restClient.patch()
				.uri("/api/orders/{id}/status", id)
				.body(request)
				.retrieve()
				.body(OrderResponse.class);
	}

	public AdminDashboardResponse getAdminDashboard() {
		return restClient.get()
				.uri("/api/orders/dashboard/admin/overview")
				.retrieve()
				.body(AdminDashboardResponse.class);
	}

	public SellerMetricsResponse getSellerMetrics(Long sellerId) {
		return restClient.get()
				.uri("/api/orders/dashboard/sellers/{sellerId}/metrics", sellerId)
				.retrieve()
				.body(SellerMetricsResponse.class);
	}

}