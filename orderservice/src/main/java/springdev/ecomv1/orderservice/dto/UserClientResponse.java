package springdev.ecomv1.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserClientResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
}
