package springdev.ecomv1.dataseeder.runner;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import springdev.ecomv1.dataseeder.client.OrderClient;
import springdev.ecomv1.dataseeder.client.PaymentClient;
import springdev.ecomv1.dataseeder.client.ProductClient;
import springdev.ecomv1.dataseeder.client.UserClient;
import springdev.ecomv1.dataseeder.dto.order.CreateOrderRequest;
import springdev.ecomv1.dataseeder.dto.order.PaymentClientResponse;
import springdev.ecomv1.dataseeder.dto.order.SellerOrderResponse;
import springdev.ecomv1.dataseeder.dto.product.CreateProductRequest;
import springdev.ecomv1.dataseeder.dto.product.ProductResponse;
import springdev.ecomv1.dataseeder.dto.user.LoginRequest;
import springdev.ecomv1.dataseeder.dto.user.LoginResponse;
import springdev.ecomv1.dataseeder.dto.user.RegisterUserRequest;
import springdev.ecomv1.dataseeder.dto.user.UserResponse;
import springdev.ecomv1.dataseeder.dto.user.UserRole;
import springdev.ecomv1.dataseeder.util.JwtStore;

@Component
@RequiredArgsConstructor
public class SeedRunner implements CommandLineRunner {

        private static final String DEFAULT_PASSWORD = "password";
        private static final String ADMIN_EMAIL = "admin@gmail.com";
        private static final String SELLER1_EMAIL = "seller1@gmail.com";
        private static final String SELLER2_EMAIL = "seller2@gmail.com";
        private static final String SELLER3_EMAIL = "seller3@gmail.com";
        private static final String CUSTOMER1_EMAIL = "customer1@gmail.com";
        private static final String CUSTOMER2_EMAIL = "customer2@gmail.com";
        private static final String CUSTOMER3_EMAIL = "customer3@gmail.com";

        private final UserClient userClient;
        private final ProductClient productClient;
        private final OrderClient orderClient;
        private final PaymentClient paymentClient;
        private final JwtStore jwtStore;
        private final SeedDataPresenceChecker seedDataPresenceChecker;

        @Override
        public void run(String... args) {
                System.out.println("Starting dataseeder flow...");

                boolean usersPresent = seedDataPresenceChecker.areSeedUsersPresent(DEFAULT_PASSWORD);
                boolean productsPresent = seedDataPresenceChecker.areSeedProductsPresent();

                // Step 1: Register all users needed for the seed scenario.
                if (!usersPresent) {
                        registerUser("Test Admin", ADMIN_EMAIL, UserRole.ADMIN);
                        registerUser("Test Seller 1", SELLER1_EMAIL, UserRole.SELLER);
                        registerUser("Test Seller 2", SELLER2_EMAIL, UserRole.SELLER);
                        registerUser("Test Seller 3", SELLER3_EMAIL, UserRole.SELLER);
                        registerUser("Test Customer 1", CUSTOMER1_EMAIL, UserRole.CUSTOMER);
                        registerUser("Test Customer 2", CUSTOMER2_EMAIL, UserRole.CUSTOMER);
                        registerUser("Test Customer 3", CUSTOMER3_EMAIL, UserRole.CUSTOMER);

                        System.out.println("✓ Registered 1 admin, 3 sellers, 3 customers.");
                } else {
                        System.out.println("✓ Users already present. Skipping user registration.");
                }

                // Step 2: Log in every user and persist their JWT for downstream calls.
                UserResponse admin = loginAndResolveUser("Test Admin", ADMIN_EMAIL, UserRole.ADMIN);
                UserResponse seller1 = loginAndResolveUser("Test Seller 1", SELLER1_EMAIL, UserRole.SELLER);
                UserResponse seller2 = loginAndResolveUser("Test Seller 2", SELLER2_EMAIL, UserRole.SELLER);
                UserResponse seller3 = loginAndResolveUser("Test Seller 3", SELLER3_EMAIL, UserRole.SELLER);
                UserResponse customer1 = loginAndResolveUser("Test Customer 1", CUSTOMER1_EMAIL, UserRole.CUSTOMER);
                UserResponse customer2 = loginAndResolveUser("Test Customer 2", CUSTOMER2_EMAIL, UserRole.CUSTOMER);
                UserResponse customer3 = loginAndResolveUser("Test Customer 3", CUSTOMER3_EMAIL, UserRole.CUSTOMER);

                System.out.println("✓ Logged in all users and saved JWTs.");

                String seller1Jwt = requireJwt(seller1.getEmail());
                String seller2Jwt = requireJwt(seller2.getEmail());
                String seller3Jwt = requireJwt(seller3.getEmail());
                String adminJwt = requireJwt(admin.getEmail());

                // Step 3: Prepare seller auth lookup and seed the product catalog.
                Map<Long, String> sellerJwtBySellerId = Map.of(
                                seller1.getId(), seller1Jwt,
                                seller2.getId(), seller2Jwt,
                                seller3.getId(), seller3Jwt);

                List<ProductResponse> createdProducts;
                if (!productsPresent) {
                        createdProducts = List.of(
                                        createProduct(seller1.getId(), seller1Jwt, new ProductSeed(
                                                        "NovaSound Elite Wireless Earbuds",
                                                        "Ergonomic in-ear Bluetooth 5.3 earbuds with IPX7 water resistance and a compact charging case.",
                                                        new BigDecimal("49.00"),
                                                        30)),
                                        createProduct(seller1.getId(), seller1Jwt, new ProductSeed(
                                                        "AeroGlide Ergonomic Wireless Mouse",
                                                        "High-precision vertical optical mouse with adjustable DPI and silent click technology.",
                                                        new BigDecimal("29.00"),
                                                        35)),
                                        createProduct(seller1.getId(), seller1Jwt, new ProductSeed(
                                                        "LuminaGlow RGB Mechanical Keyboard",
                                                        "Hot-swappable tactile mechanical keyboard with customizable per-key RGB backlighting.",
                                                        new BigDecimal("89.00"),
                                                        20)),

                                        createProduct(seller2.getId(), seller2Jwt, new ProductSeed(
                                                        "TitanView 27-Inch Curved Monitor",
                                                        "144Hz refresh rate 2K QHD gaming monitor with 1ms response time and AMD FreeSync support.",
                                                        new BigDecimal("249.00"),
                                                        12)),
                                        createProduct(seller2.getId(), seller2Jwt, new ProductSeed(
                                                        "VoltStream 65W GaN Fast Charger",
                                                        "Ultra-compact multi-port wall charger capable of powering laptops, tablets, and phones simultaneously.",
                                                        new BigDecimal("35.00"),
                                                        40)),
                                        createProduct(seller2.getId(), seller2Jwt, new ProductSeed(
                                                        "ZenithCharge Magnetic Wireless Power Bank",
                                                        "10000mAh portable charger with MagSafe compatibility and integrated kickstand.",
                                                        new BigDecimal("42.00"),
                                                        25)),

                                        createProduct(seller3.getId(), seller3Jwt, new ProductSeed(
                                                        "StratoCam 4K Ultra HD Webcam",
                                                        "Professional streaming camera featuring dual noise-canceling microphones and automatic low-light correction.",
                                                        new BigDecimal("68.00"),
                                                        18)),
                                        createProduct(seller3.getId(), seller3Jwt, new ProductSeed(
                                                        "OmniDesk Dual Monitor Mount",
                                                        "Heavy-duty full-motion articulating gas spring desktop arm for monitors up to 32 inches.",
                                                        new BigDecimal("55.00"),
                                                        22)),
                                        createProduct(seller3.getId(), seller3Jwt, new ProductSeed(
                                                        "SonicWave Studio Condenser Microphone",
                                                        "Cardioid USB microphone kit with boom arm and pop filter, optimized for podcasting and streaming.",
                                                        new BigDecimal("79.00"),
                                                        16)),

                                        createProduct(seller1.getId(), seller1Jwt, new ProductSeed(
                                                        "NovaSound Elite Wireless Earbuds - Limited",
                                                        "Limited run variant of NovaSound earbuds with the same ergonomic profile and water resistance.",
                                                        new BigDecimal("49.00"),
                                                        15)));

                        System.out.println("✓ Created 10 products (seller-only flow).");
                } else {
                        createdProducts = seedDataPresenceChecker.getSeedProductsInExpectedOrder();
                        System.out.println("✓ Products already present. Skipping product creation.");
                }

                // Step 4: Split seeded products across customers and place orders.
                String customer1Jwt = requireJwt(customer1.getEmail());
                String customer2Jwt = requireJwt(customer2.getEmail());
                String customer3Jwt = requireJwt(customer3.getEmail());

                List<ProductResponse> productsForCustomer1 = List.of(
                                createdProducts.get(0),
                                createdProducts.get(4),
                                createdProducts.get(8));
                List<ProductResponse> productsForCustomer2 = List.of(
                                createdProducts.get(1),
                                createdProducts.get(5),
                                createdProducts.get(6));
                List<ProductResponse> productsForCustomer3 = List.of(
                                createdProducts.get(2),
                                createdProducts.get(3),
                                createdProducts.get(9));

                createOrdersForCustomer(customer1.getId(), customer1Jwt, productsForCustomer1);
                createOrdersForCustomer(customer2.getId(), customer2Jwt, productsForCustomer2);
                createOrdersForCustomer(customer3.getId(), customer3Jwt, productsForCustomer3);

                int totalCreatedOrders = productsForCustomer1.size() + productsForCustomer2.size() + productsForCustomer3.size();
                System.out.println("✓ Created " + totalCreatedOrders + " purchase orders (customer-only flow).");

                // Step 5: Verify payment records for every newly created order.
                int verifiedPayments = 0;
                verifiedPayments += verifyPaymentsForCustomer(
                                customer1.getId(), productsForCustomer1, sellerJwtBySellerId, adminJwt);
                verifiedPayments += verifyPaymentsForCustomer(
                                customer2.getId(), productsForCustomer2, sellerJwtBySellerId, adminJwt);
                verifiedPayments += verifyPaymentsForCustomer(
                                customer3.getId(), productsForCustomer3, sellerJwtBySellerId, adminJwt);

                if (verifiedPayments == totalCreatedOrders) {
                        System.out.println("✓ Payment service verified for all " + verifiedPayments + " orders.");
                }

                // Step 6: Finish the end-to-end data seeding workflow.
                System.out.println("✓ Completed full seed flow.");
        }

        private UserResponse registerUser(String name, String email, UserRole role) {
                return userClient.register(
                                RegisterUserRequest.builder()
                                                .name(name)
                                                .email(email)
                                                .password(DEFAULT_PASSWORD)
                                                .role(role)
                                                .build());
        }

        private UserResponse loginAndResolveUser(String name, String email, UserRole role) {
                LoginResponse loginResponse = userClient.login(
                                LoginRequest.builder()
                                                .email(email)
                                                .password(DEFAULT_PASSWORD)
                                                .build());

                jwtStore.save(loginResponse.getEmail(), loginResponse.getToken());

                UserResponse user = new UserResponse();
                user.setId(loginResponse.getUserId());
                user.setEmail(loginResponse.getEmail());
                user.setName(name);
                user.setRole(role);
                return user;
        }

        private String requireJwt(String email) {
                String token = jwtStore.get(email);
                if (token == null || token.isBlank()) {
                        throw new IllegalStateException("Missing JWT token for user: " + email);
                }
                return token;
        }

        private ProductResponse createProduct(Long sellerId, String sellerJwt, ProductSeed productSeed) {
                return productClient.createProduct(
                                CreateProductRequest.builder()
                                                .name(productSeed.name())
                                                .description(productSeed.description())
                                                .price(productSeed.price())
                                                .sellerId(sellerId)
                                                .quantity(productSeed.quantity())
                                                .build(),
                                sellerJwt);
        }

        private PaymentClientResponse fetchPaymentBySellerView(
                        Long sellerId,
                        String sellerJwt,
                        Long customerId,
                        Long productId,
                        String paymentLookupJwt) {
                List<SellerOrderResponse> sellerOrders = orderClient.getOrdersBySellerId(sellerId, sellerJwt);

                Long orderId = sellerOrders.stream()
                                .filter(order -> Objects.equals(order.getCustomerId(), customerId)
                                                && Objects.equals(order.getProductId(), productId))
                                .max(Comparator.comparing(SellerOrderResponse::getCreatedAt))
                                .map(SellerOrderResponse::getOrderId)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Unable to resolve order id for customerId=" + customerId + " and productId=" + productId));

                return paymentClient.getPaymentByOrderId(orderId, paymentLookupJwt);
        }

        private void createOrdersForCustomer(Long customerId, String customerJwt, List<ProductResponse> selectedProducts) {
                for (ProductResponse selectedProduct : selectedProducts) {
                        orderClient.createOrder(CreateOrderRequest.builder()
                                        .customerId(customerId)
                                        .productId(selectedProduct.getId())
                                        .quantity(1)
                                        .build(), customerJwt);
                }
        }

        private int verifyPaymentsForCustomer(
                        Long customerId,
                        List<ProductResponse> selectedProducts,
                        Map<Long, String> sellerJwtBySellerId,
                        String paymentLookupJwt) {
                int verifiedCount = 0;

                for (ProductResponse selectedProduct : selectedProducts) {
                        Long sellerId = selectedProduct.getSellerId();
                        String sellerJwt = sellerJwtBySellerId.get(sellerId);
                        if (sellerJwt == null || sellerJwt.isBlank()) {
                                throw new IllegalStateException("Missing JWT token for sellerId: " + sellerId);
                        }

                        PaymentClientResponse payment = fetchPaymentBySellerView(
                                        sellerId,
                                        sellerJwt,
                                        customerId,
                                        selectedProduct.getId(),
                                        paymentLookupJwt);

                        if (payment != null) {
                                verifiedCount++;
                        }
                }

                return verifiedCount;
        }

        private record ProductSeed(
                        @NonNull String name,
                        @NonNull String description,
                        @NonNull BigDecimal price,
                        @NonNull Integer quantity) {

        }
}
