package springdev.ecomv1.dataseeder.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import springdev.ecomv1.dataseeder.client.ProductClient;
import springdev.ecomv1.dataseeder.client.UserClient;
import springdev.ecomv1.dataseeder.dto.product.ProductResponse;
import springdev.ecomv1.dataseeder.dto.user.LoginRequest;

@Component
@RequiredArgsConstructor
public class SeedDataPresenceChecker {

    private static final List<String> EXPECTED_SEED_USER_EMAILS = List.of(
            "admin@gmail.com",
            "seller1@gmail.com",
            "seller2@gmail.com",
            "seller3@gmail.com",
            "customer1@gmail.com",
            "customer2@gmail.com",
            "customer3@gmail.com");

    private static final List<String> EXPECTED_PRODUCT_NAMES = List.of(
            "NovaSound Elite Wireless Earbuds",
            "AeroGlide Ergonomic Wireless Mouse",
            "LuminaGlow RGB Mechanical Keyboard",
            "TitanView 27-Inch Curved Monitor",
            "VoltStream 65W GaN Fast Charger",
            "ZenithCharge Magnetic Wireless Power Bank",
            "StratoCam 4K Ultra HD Webcam",
            "OmniDesk Dual Monitor Mount",
            "SonicWave Studio Condenser Microphone",
            "NovaSound Elite Wireless Earbuds - Limited");

    private final ProductClient productClient;
    private final UserClient userClient;

    public boolean areSeedUsersPresent(String defaultPassword) {
        for (String email : EXPECTED_SEED_USER_EMAILS) {
            try {
                userClient.login(LoginRequest.builder()
                        .email(email)
                        .password(defaultPassword)
                        .build());
            } catch (RuntimeException ex) {
                return false;
            }
        }
        return true;
    }

    public boolean areSeedProductsPresent() {
        List<ProductResponse> existingProducts = productClient.getAllProducts();
        if (existingProducts == null || existingProducts.isEmpty()) {
            return false;
        }

        List<String> existingProductNames = existingProducts.stream()
                .map(ProductResponse::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return existingProductNames.containsAll(EXPECTED_PRODUCT_NAMES);
    }

    public List<ProductResponse> getSeedProductsInExpectedOrder() {
        List<ProductResponse> allProducts = productClient.getAllProducts();
        Map<String, ProductResponse> productByName = allProducts.stream()
                .filter(product -> product.getName() != null)
                .collect(Collectors.toMap(ProductResponse::getName, product -> product, (first, second) -> first));

        List<ProductResponse> orderedProducts = new ArrayList<>();
        for (String expectedProductName : EXPECTED_PRODUCT_NAMES) {
            ProductResponse product = productByName.get(expectedProductName);
            if (product == null) {
                throw new IllegalStateException("Expected seeded product not found: " + expectedProductName);
            }
            orderedProducts.add(product);
        }
        return orderedProducts;
    }
}
