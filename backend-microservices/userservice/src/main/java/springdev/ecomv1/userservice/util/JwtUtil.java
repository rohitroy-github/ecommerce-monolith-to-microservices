package springdev.ecomv1.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import springdev.ecomv1.userservice.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JwtUtil - JWT Token Management Utility
 * 
 * Handles generation, validation, and claims extraction for JWT tokens.
 * Uses HMAC-SHA256 for signing and JJWT library (v0.12.7) for all operations.
 * 
 * Token Structure:
 * - Subject (sub): User's email - used as primary identifier
 * - Claims: userId, role - additional user information
 * - IssuedAt (iat): Token creation timestamp
 * - Expiration (exp): Token validity end time (default: 24 hours)
 * - Signature: HMAC-SHA256 signed with secret key
 */
@Component
public class JwtUtil {

    private final String jwtSecret;
    
    private final long jwtExpiration;
    
    private final SecretKey key;

    /**
     * Constructor - Initializes JWT configuration from application properties
     * 
     * @param jwtSecret JWT signing secret from jwt.secret property
     * @param jwtExpiration Token lifetime in milliseconds from jwt.expiration property
     */
    public JwtUtil(@Value("${jwt.secret}") String jwtSecret, 
                   @Value("${jwt.expiration}") long jwtExpiration) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
        // Pre-compute the SecretKey from bytes for HMAC-SHA256 signing
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT Token for authenticated user
     * 
     * Token contains:
     * - Email as subject (main identifier for lookups)
     * - userId claim (database ID for user queries)
     * - role claim (user's permission level: CUSTOMER, SELLER, ADMIN)
     * - issuedAt (creation timestamp)
     * - expiration (auto-expires after jwtExpiration milliseconds)
     * 
     * @param user The authenticated User entity
     * @return Compact JWT token string (can be sent to client)
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())                          // Primary identifier
                .claim("userId", user.getId())                      // Additional claim: user ID
                .claim("role", user.getRole().toString())           // Additional claim: user role
                .issuedAt(new Date())                               // When token was created
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))  // Auto-expiry
                .signWith(key, SignatureAlgorithm.HS256)            // Sign with HMAC-SHA256
                .compact();                                         // Serialize to compact form
    }

    /**
     * Extract user email from valid JWT token
     * 
     * Useful for extracting user identity from Authorization header
     * during request processing (filters, interceptors, etc.)
     * 
     * @param token JWT token string (typically from Bearer token)
     * @return User's email extracted from token subject
     * @throws JwtException if token is invalid, expired, or signature verification fails
     */
    public String extractEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Validate JWT token - checks signature and expiration
     * 
     * Returns boolean (safe) instead of throwing exceptions.
     * Useful for:
     * - Pre-validation in filters before processing
     * - Token refresh checks
     * - Authorization header validation
     * 
     * @param token JWT token to validate
     * @return true if token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Attempt to parse and verify token
            // Will throw exception if signature is invalid or token is expired
            getClaims(token);
            return true;
        } catch (Exception ex) {
            // Token is invalid, expired, or tampered with
            return false;
        }
    }

    /**
     * Internal helper - Parse and verify JWT token
     * 
     * This method:
     * 1. Parses the compact JWT string
     * 2. Verifies the HMAC-SHA256 signature using our secret key
     * 3. Extracts claims from the payload
     * 4. Throws exception if signature doesn't match (token tampering detected)
     * 5. Throws exception if token is expired
     * 
     * @param token JWT token string
     * @return Claims object containing all token data (subject, custom claims, exp, iat)
     * @throws JwtException if signature verification fails or token is expired
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)                    // Verify signature with our secret key
                .build()                            // Build parser
                .parseSignedClaims(token)           // Parse and verify JWT
                .getPayload();                      // Extract claims from token body
    }
}
