package springdev.ecomv1.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import springdev.ecomv1.apigateway.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // Custom gateway filter that validates JWTs before requests are routed downstream.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    // Gateway security is stateless because authentication is carried entirely by JWTs.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                // No HTTP session should be created for token-based authentication.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Route-level access control is handled by the JWT filter itself.
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Ensure JWT validation runs before Spring Security's username/password flow.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}