package springdev.ecomv1.dataseeder.dto.user;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

	private Long id;
	private String name;
	private String email;
	private UserRole role;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}