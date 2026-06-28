package springdev.ecomv1.apigateway.enums;

/**
 * RoleType - Authorization roles for route access control.
 * 
 * - CUSTOMER: Can browse products and manage personal orders
 * - SELLER: Can manage product catalog
 * - ADMIN: Full system access
 */
public enum RoleType {
    CUSTOMER("Customer role - browse products and manage orders"),
    SELLER("Seller role - create and manage products"),
    ADMIN("Admin role - full access to all resources");

    private final String description;

    RoleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Convert string to RoleType. Returns null if string doesn't match any role.
     */
    public static RoleType fromString(String role) {
        if (role == null) {
            return null;
        }
        try {
            return RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
