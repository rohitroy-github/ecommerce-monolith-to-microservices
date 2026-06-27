package springdev.ecomv1.productservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import springdev.ecomv1.productservice.dto.CreateProductRequest;
import springdev.ecomv1.productservice.dto.ProductAvailabilityResponse;
import springdev.ecomv1.productservice.dto.ProductResponse;
import springdev.ecomv1.productservice.dto.ProductSummaryResponse;
import springdev.ecomv1.productservice.dto.ReduceStockRequest;
import springdev.ecomv1.productservice.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Creates a new product and returns 201 with the persisted payload.
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // Returns the full product list as a simple read endpoint.
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Returns a single product by id and 404 when not found.
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // Returns products for a seller with a compact payload (id and name only).
    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<List<ProductSummaryResponse>> getProductsBySellerId(@PathVariable Long sellerId) {
        List<ProductSummaryResponse> products = productService.getProductsBySellerId(sellerId);
        return ResponseEntity.ok(products);
    }

    // Returns availability details for the given product id.
    @GetMapping("/{id}/availability")
    public ResponseEntity<ProductAvailabilityResponse> getProductAvailability(@PathVariable Long id) {
        ProductAvailabilityResponse availability = productService.getProductAvailability(id);
        return ResponseEntity.ok(availability);
    }

    // Reduces stock for the given product id.
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductAvailabilityResponse> reduceStock(
            @PathVariable Long id,
            @Valid @RequestBody ReduceStockRequest request) {
        ProductAvailabilityResponse availability = productService.reduceStock(id, request.getQuantity());
        return ResponseEntity.ok(availability);
    }
}