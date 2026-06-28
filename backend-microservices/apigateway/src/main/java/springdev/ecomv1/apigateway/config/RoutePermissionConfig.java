package springdev.ecomv1.apigateway.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import springdev.ecomv1.apigateway.enums.RoleType;

/**
 * RoutePermissionConfig - Role-based route access control.
 * 
 * Defines which HTTP methods on which routes are allowed for each role.
 * Format: "METHOD:/path" (e.g., "GET:/api/products")
 * 
 * Roles:
 * - CUSTOMER: Can view products and manage their own orders
 * - SELLER: Can view, create, update, delete their products
 * - ADMIN: Full access to all routes
 */
@Component
public class RoutePermissionConfig {

    private static final Logger logger = LoggerFactory.getLogger(RoutePermissionConfig.class);

    private final Map<RoleType, List<String>> permissions = new HashMap<>();

    public RoutePermissionConfig() {
        // Customer: Browse catalog and manage orders
        permissions.put(RoleType.CUSTOMER, List.of(
                "GET:/api/products",
                "GET:/api/products/**",
                "POST:/api/orders",
                "GET:/api/orders/**"));

        // Seller: Manage product catalog
        permissions.put(RoleType.SELLER, List.of(
                "GET:/api/products",
                "GET:/api/products/**",
                "GET:/api/products/sellers/**",
                "GET:/api/orders/sellers/**",
                "POST:/api/products",
                "PUT:/api/products/**",
                "DELETE:/api/products/**",
                "GET:/api/orders/dashboard/**",
                "POST:/api/orders/dashboard/**"));

        // Admin: Full access
        permissions.put(RoleType.ADMIN, List.of("/**"));
    }

    public Map<RoleType, List<String>> getPermissions() {
        return permissions;
    }

    /**
     * Check if a role has permission for a route.
     * 
     * @param roleStr User role as string (CUSTOMER, SELLER, ADMIN)
     * @param method  HTTP method (GET, POST, PUT, DELETE)
     * @param path    Request path
     * @return true if allowed, false otherwise
     */
    public boolean hasPermission(String roleStr, String method, String path) {
        // Convert string role to RoleType enum
        logger.info("Visiting route - Method: {}, Path: {}, Role: {}", method, path, roleStr);
        
        RoleType role = RoleType.fromString(roleStr);
        if (role == null) {
            logger.warn("Invalid role provided: {}", roleStr);
            return false;
        }

        // Seller-order routes are restricted to SELLER and ADMIN roles.
        if (path.startsWith("/api/orders/sellers/")) {
            boolean hasAccess = role == RoleType.SELLER || role == RoleType.ADMIN;
            logger.info("Seller-order route access for role {}: {}", role, hasAccess);
            return hasAccess;
        }

        List<String> rolePermissions = permissions.get(role);
        if (rolePermissions == null) {
            logger.warn("No permissions found for role: {}", role);
            return false;
        }

        String routePattern = method + ":" + path;

        for (String permission : rolePermissions) {
            // Full wildcard access
            if (permission.equals("/**")) {
                logger.debug("Full wildcard access granted for role: {}", role);
                return true;
            }

            // Exact match
            if (permission.equals(routePattern)) {
                logger.info("Route access ALLOWED - Role: {}, Route: {}", role, routePattern);
                return true;
            }

            // Wildcard path matching (e.g., "GET:/api/orders/**" matches
            // "GET:/api/orders/1")
            if (permission.endsWith("/**")) {
                String basePath = permission.substring(0, permission.length() - 2);
                if (routePattern.startsWith(basePath)) {
                    logger.info("Route access ALLOWED - Role: {}, Route: {}", role, routePattern);
                    return true;
                }
            }
        }

        logger.warn("Route access DENIED - Role: {}, Route: {}", role, routePattern);
        return false;
    }
}
