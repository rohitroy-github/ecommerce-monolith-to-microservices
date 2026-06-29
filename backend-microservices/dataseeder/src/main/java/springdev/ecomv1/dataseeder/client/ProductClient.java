package springdev.ecomv1.dataseeder.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import springdev.ecomv1.dataseeder.dto.product.CreateProductRequest;
import springdev.ecomv1.dataseeder.dto.product.ProductAvailabilityResponse;
import springdev.ecomv1.dataseeder.dto.product.ProductResponse;
import springdev.ecomv1.dataseeder.dto.product.ProductSummaryResponse;
import springdev.ecomv1.dataseeder.dto.product.ReduceStockRequest;

@Component
public class ProductClient {

	private final RestClient restClient;

	public ProductClient(@Qualifier("productRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public RestClient restClient() {
		return restClient;
	}

	public ProductResponse createProduct(CreateProductRequest request) {
		return restClient.post()
				.uri("/api/products")
				.body(request)
				.retrieve()
				.body(ProductResponse.class);
	}

	public ProductResponse createProduct(CreateProductRequest request, String jwtToken) {
		return restClient.post()
				.uri("/api/products")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
				.body(request)
				.retrieve()
				.body(ProductResponse.class);
	}

	public List<ProductResponse> getAllProducts() {
		return restClient.get()
				.uri("/api/products")
				.retrieve()
				.body(new ParameterizedTypeReference<List<ProductResponse>>() {
				});
	}

	public ProductResponse getProductById(Long id) {
		return restClient.get()
				.uri("/api/products/{id}", id)
				.retrieve()
				.body(ProductResponse.class);
	}

	public List<ProductSummaryResponse> getProductsBySellerId(Long sellerId) {
		return restClient.get()
				.uri("/api/products/sellers/{sellerId}", sellerId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<ProductSummaryResponse>>() {
				});
	}

	public ProductAvailabilityResponse getProductAvailability(Long id) {
		return restClient.get()
				.uri("/api/products/{id}/availability", id)
				.retrieve()
				.body(ProductAvailabilityResponse.class);
	}

	public ProductAvailabilityResponse reduceStock(Long id, ReduceStockRequest request) {
		return restClient.put()
				.uri("/api/products/{id}/stock", id)
				.body(request)
				.retrieve()
				.body(ProductAvailabilityResponse.class);
	}

}