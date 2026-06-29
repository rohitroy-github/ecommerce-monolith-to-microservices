package springdev.ecomv1.dataseeder.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class JwtStore {

	private final Map<String, String> tokens = new HashMap<>();

	public synchronized void save(String email, String jwt) {
		tokens.put(email, jwt);
	}

	public synchronized String get(String email) {
		return tokens.get(email);
	}

}