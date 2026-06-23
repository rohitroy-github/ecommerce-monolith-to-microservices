package springdev.ecomv1.userservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import springdev.ecomv1.userservice.dto.LoginRequest;
import springdev.ecomv1.userservice.dto.LoginResponse;
import springdev.ecomv1.userservice.dto.RegisterUserRequest;
import springdev.ecomv1.userservice.dto.UserResponse;
import springdev.ecomv1.userservice.entity.User;
import springdev.ecomv1.userservice.exception.ConflictException;
import springdev.ecomv1.userservice.exception.ResourceNotFoundException;
import springdev.ecomv1.userservice.repository.UserRepository;
import springdev.ecomv1.userservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserResponse register(RegisterUserRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email already exsists in our system. Please use a different email address.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());
        response.setUpdatedAt(savedUser.getUpdatedAt());
        return response;
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No user found in our system with ID: " + id));

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setCreatedAt(user.getCreatedAt());
            response.setUpdatedAt(user.getUpdatedAt());
            return response;
        }).collect(Collectors.toList());
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Email address doesn't exist in our system. Please register first."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials. Please check your email and password.");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, user.getEmail(), user.getId(), user.getRole().toString());
    }
}
