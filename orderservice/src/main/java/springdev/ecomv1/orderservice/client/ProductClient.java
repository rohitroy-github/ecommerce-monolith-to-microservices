package springdev.ecomv1.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import springdev.ecomv1.orderservice.dto.ProductAvailabilityResponse;
import springdev.ecomv1.orderservice.dto.ProductClientResponse;
import springdev.ecomv1.orderservice.dto.ReduceStockRequest;

@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductClientResponse getProductById(@PathVariable Long id);

    @GetMapping("/api/products/{id}/availability")
    ProductAvailabilityResponse getProductAvailability(@PathVariable Long id);

    @PutMapping("/api/products/{id}/stock")
    ProductAvailabilityResponse reduceStock(@PathVariable Long id, @RequestBody ReduceStockRequest request);
}
