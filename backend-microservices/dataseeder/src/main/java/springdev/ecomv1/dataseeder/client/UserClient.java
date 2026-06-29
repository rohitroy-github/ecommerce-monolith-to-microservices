package springdev.ecomv1.dataseeder.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import springdev.ecomv1.dataseeder.dto.user.LoginRequest;
import springdev.ecomv1.dataseeder.dto.user.LoginResponse;
import springdev.ecomv1.dataseeder.dto.user.RegisterUserRequest;
import springdev.ecomv1.dataseeder.dto.user.UserResponse;

@Component
public class UserClient {

	private final RestClient restClient;

	public UserClient(@Qualifier("userRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public RestClient restClient() {
		return restClient;
	}

	public UserResponse register(RegisterUserRequest request) {
		return restClient.post()
				.uri("/api/users/register")
				.body(request)
				.retrieve()
				.body(UserResponse.class);
	}

	public LoginResponse login(LoginRequest request) {
		return restClient.post()
				.uri("/api/users/login")
				.body(request)
				.retrieve()
				.body(LoginResponse.class);
	}

}