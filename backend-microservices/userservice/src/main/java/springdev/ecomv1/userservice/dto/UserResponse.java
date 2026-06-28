package springdev.ecomv1.userservice.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import springdev.ecomv1.userservice.enums.UserRole;

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
