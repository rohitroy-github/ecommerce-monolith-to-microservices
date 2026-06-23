package springdev.ecomv1.apigateway.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import springdev.ecomv1.apigateway.service.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Workflow Step 1: Allow onboarding routes to pass without JWT.
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/users/register",
            "/api/users/login");

    private static final String PRODUCTS_PATH = "/api/products";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    // Workflow Entry: Every incoming request is evaluated here before routing.
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Workflow Step 2: Resolve request path.
        String path = request.getRequestURI();

        // Workflow Step 3: If request is public, skip JWT checks and continue.
        if (isPublicRoute(request, path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Workflow Step 4: Read Authorization header from request.
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Workflow Step 5: Protected endpoints require a Bearer token, else return 401.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, request, "Authorization header is missing or invalid");
            return;
        }

        // Workflow Step 6: Extract raw JWT by removing "Bearer " prefix.
        String token = authHeader.substring(7);

        // Workflow Step 7: Validate token integrity and expiry, else return 401.
        if (!jwtService.isValid(token)) {
            writeUnauthorized(response, request, "JWT token is invalid or expired");
            return;
        }

        // Workflow Step 8: Token is valid, proceed to downstream routing.
        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request, String path) {
        if (PUBLIC_ENDPOINTS.contains(path)) {
            return true;
        }

        // Product catalog reads are public; write operations still require JWT.
        return HttpMethod.GET.matches(request.getMethod())
                && (PRODUCTS_PATH.equals(path) || path.startsWith(PRODUCTS_PATH + "/"));
    }

    private void writeUnauthorized(HttpServletResponse response, HttpServletRequest request, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                Instant.now(),
                escapeJson(message),
                escapeJson(request.getRequestURI()));

        response.getWriter().write(body);
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}