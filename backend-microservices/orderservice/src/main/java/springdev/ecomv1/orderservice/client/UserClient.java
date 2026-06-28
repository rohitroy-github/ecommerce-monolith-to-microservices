package springdev.ecomv1.orderservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import springdev.ecomv1.orderservice.dto.UserClientResponse;

@FeignClient(name = "user-service", url = "${services.user.url}")
public interface UserClient {

    @GetMapping("/api/users")
    List<UserClientResponse> getAllUsers();

    @GetMapping("/api/users/{id}")
    UserClientResponse getUserById(@PathVariable Long id);
}
