package springdev.ecomv1.apigateway.service;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;

	public JwtService(@Value("${jwt.secret}") String jwtSecret) {
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
	}

	public boolean isValid(String token) {
		try {
			getClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public String extractEmail(String token) {
		return getClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return getClaims(token).get("role", String.class);
	}

	private Claims getClaims(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
