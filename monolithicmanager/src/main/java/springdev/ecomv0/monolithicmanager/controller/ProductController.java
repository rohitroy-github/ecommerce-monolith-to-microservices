package springdev.ecomv0.monolithicmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import springdev.ecomv0.monolithicmanager.dto.CreateProductRequest;
import springdev.ecomv0.monolithicmanager.dto.ProductAvailiblityResponse;
import springdev.ecomv0.monolithicmanager.dto.ProductResponse;
import springdev.ecomv0.monolithicmanager.dto.ReduceStockRequest;
import springdev.ecomv0.monolithicmanager.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Creates a new product and its inventory record.
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Returns all products.
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // Returns one product by its ID.
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    // Returns availability details for a product.
    @GetMapping("/{productId}/availability")
    public ResponseEntity<ProductAvailiblityResponse> getProductAvailibility(@PathVariable Long productId) {
        ProductAvailiblityResponse response = productService.getProductAvailibility(productId);
        return ResponseEntity.ok(response);
    }

    // Reduces product stock by the requested quantity.
    @PutMapping("/{productId}/stock")
    public ResponseEntity<ProductAvailiblityResponse> reduceProductStock(@PathVariable Long productId,
            @Valid @RequestBody ReduceStockRequest request) {
        ProductAvailiblityResponse response = productService.reduceProductStock(productId, request.getQuantity());
        return ResponseEntity.ok(response);
    }

    // Returns all products for a given seller.
    @GetMapping("/seller/{sellerId}")
    public List<ProductResponse> getProductsBySellerId(@PathVariable Long sellerId) {
        return productService.getProductsBySellerId(sellerId);
    }

}
