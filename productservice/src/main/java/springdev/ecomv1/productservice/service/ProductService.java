    package springdev.ecomv1.productservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import springdev.ecomv1.productservice.dto.CreateProductRequest;
import springdev.ecomv1.productservice.dto.ProductAvailabilityResponse;
import springdev.ecomv1.productservice.dto.ProductResponse;
import springdev.ecomv1.productservice.entity.Inventory;
import springdev.ecomv1.productservice.entity.Product;
import springdev.ecomv1.productservice.exception.ConflictException;
import springdev.ecomv1.productservice.exception.ResourceNotFoundException;
import springdev.ecomv1.productservice.repository.InventoryRepository;
import springdev.ecomv1.productservice.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final InventoryRepository inventoryRepository;

        @Transactional
        public ProductResponse createProduct(CreateProductRequest request) {
                LocalDateTime now = LocalDateTime.now();

                Product product = Product.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .price(request.getPrice())
                                .sellerId(request.getSellerId())
                                .createdAt(now)
                                .updatedAt(now)
                                .build();

                Product savedProduct = productRepository.save(product);

                Inventory inventory = Inventory.builder()
                                .productId(savedProduct.getId())
                                .quantity(request.getQuantity())
                                .updatedAt(now)
                                .build();

                inventoryRepository.save(inventory);

                return ProductResponse.builder()
                                .id(savedProduct.getId())
                                .name(savedProduct.getName())
                                .description(savedProduct.getDescription())
                                .price(savedProduct.getPrice())
                                .sellerId(savedProduct.getSellerId())
                                .createdAt(savedProduct.getCreatedAt())
                                .updatedAt(savedProduct.getUpdatedAt())
                                .build();
        }

        @Transactional(readOnly = true)
        public List<ProductResponse> getAllProducts() {
                return productRepository.findAll().stream()
                                .map(product -> ProductResponse.builder()
                                                .id(product.getId())
                                                .name(product.getName())
                                                .description(product.getDescription())
                                                .price(product.getPrice())
                                                .sellerId(product.getSellerId())
                                                .createdAt(product.getCreatedAt())
                                                .updatedAt(product.getUpdatedAt())
                                                .build())
                                .toList();
        }

        @Transactional(readOnly = true)
        public ProductResponse getProductById(Long id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

                return ProductResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .sellerId(product.getSellerId())
                                .createdAt(product.getCreatedAt())
                                .updatedAt(product.getUpdatedAt())
                                .build();
        }

        @Transactional(readOnly = true)
        public ProductAvailabilityResponse getProductAvailability(Long id) {
                productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

                Inventory inventory = inventoryRepository.findByProductId(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));

                int quantity = inventory.getQuantity() == null ? 0 : inventory.getQuantity();

                return ProductAvailabilityResponse.builder()
                                .productId(id)
                                .quantity(quantity)
                                .available(quantity > 0)
                                .build();
        }

        @Transactional
        public ProductAvailabilityResponse reduceStock(Long id, Integer quantityToReduce) {
                productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

                Inventory inventory = inventoryRepository.findByProductId(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));

                int currentQuantity = inventory.getQuantity() == null ? 0 : inventory.getQuantity();

                if (currentQuantity < quantityToReduce) {
                        throw new ConflictException("Insufficient stock for product id: " + id);
                }

                int updatedQuantity = currentQuantity - quantityToReduce;
                inventory.setQuantity(updatedQuantity);
                inventory.setUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(inventory);

                return ProductAvailabilityResponse.builder()
                                .productId(id)
                                .quantity(updatedQuantity)
                                .available(updatedQuantity > 0)
                                .build();
        }
}