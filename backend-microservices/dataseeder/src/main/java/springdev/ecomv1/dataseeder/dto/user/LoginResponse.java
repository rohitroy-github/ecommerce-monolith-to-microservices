package springdev.ecomv1.dataseeder.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

	private String token;
	private String email;
	private Long userId;
	private String role;

	public LoginResponse(String token, String email, Long userId, String role) {
		this.token = token;
		this.email = email;
		this.userId = userId;
		this.role = role;
	}
}