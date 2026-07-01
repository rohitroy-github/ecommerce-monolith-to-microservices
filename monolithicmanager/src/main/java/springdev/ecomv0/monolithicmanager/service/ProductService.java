package springdev.ecomv0.monolithicmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import springdev.ecomv0.monolithicmanager.dto.CreateProductRequest;
import springdev.ecomv0.monolithicmanager.dto.ProductAvailiblityResponse;
import springdev.ecomv0.monolithicmanager.dto.ProductResponse;
import springdev.ecomv0.monolithicmanager.entity.Inventory;
import springdev.ecomv0.monolithicmanager.entity.Product;
import springdev.ecomv0.monolithicmanager.exception.ResourceNotFoundException;
import springdev.ecomv0.monolithicmanager.repository.InventoryRepository;
import springdev.ecomv0.monolithicmanager.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final InventoryRepository inventoryRepository;

        /**
         * Creates a new product and its associated inventory.
         *
         * @param request the request containing product and inventory details
         * @return the response containing the created product details
         */
        @Transactional
        public ProductResponse createProduct(CreateProductRequest request) {

                Product product = Product.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .price(request.getPrice())
                                .sellerId(request.getSellerId())
                                .build();

                Product savedProduct = productRepository.save(product);

                Inventory inventory = Inventory.builder()
                                .productId(savedProduct.getId())
                                .quantity(request.getQuantity())
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

        /**
         * Retrieves all products from the database.
         *
         * @return a list of product responses
         */
        @Transactional(readOnly = true)
        public List<ProductResponse> getAllProducts() {
                List<Product> fetchedProducts = productRepository.findAll();
                return fetchedProducts.stream()
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

        /**
         * Retrieves a product by its ID.
         *
         * @param productId the ID of the product to retrieve
         * @return the response containing the product details
         */
        @Transactional(readOnly = true)
        public ProductResponse getProductById(Long productId) {
                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

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

        /**
         * Retrieves the availability of a product by its ID.
         *
         * @param productId the ID of the product to check availability for
         * @return the response containing the product availability details
         */
        @Transactional(readOnly = true)
        public ProductAvailiblityResponse getProductAvailibility(Long productId) {
                Inventory inventory = inventoryRepository.findByProductId(productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inventory not found for product id: " + productId));

                boolean available = inventory.getQuantity() > 0;

                return ProductAvailiblityResponse.builder()
                                .productId(inventory.getProductId())
                                .quantity(inventory.getQuantity() == 0 ? 0 : inventory.getQuantity())
                                .available(available)
                                .build();
        }

        /**
         * Reduces the stock of a product by its ID.
         *
         * @param productId        the ID of the product to reduce stock for
         * @param quantityToReduce the quantity to reduce from the stock
         * @return the response containing the updated product availability details
         */
        @Transactional
        public ProductAvailiblityResponse reduceProductStock(Long productId, int quantityToReduce) {
                Inventory inventory = inventoryRepository.findByProductId(productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inventory not found for product id: " + productId));

                if (inventory.getQuantity() <= 0) {
                        throw new RuntimeException("Product is out of stock for product id: " + productId);
                }

                inventory.setQuantity(inventory.getQuantity() - quantityToReduce);
                inventoryRepository.save(inventory);

                boolean available = inventory.getQuantity() > 0;

                return ProductAvailiblityResponse.builder()
                                .productId(inventory.getProductId())
                                .quantity(inventory.getQuantity())
                                .available(available)
                                .build();
        }

        /**
         * Retrieves products by the seller's ID.
         *
         * @param sellerId the ID of the seller to retrieve products for
         * @return a list of product responses associated with the seller
         */
        @Transactional(readOnly = true)
        public List<ProductResponse> getProductsBySellerId(Long sellerId) {
                List<Product> fetchedProducts = productRepository.findBySellerId(sellerId);
                return fetchedProducts.stream()
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
}
