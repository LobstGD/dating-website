package com.example.datingWebsite.service;

import com.example.datingWebsite.dto.UserRequest;
import com.example.datingWebsite.dto.UserResponse;
import com.example.datingWebsite.exception.EmailAlreadyExistsException;
import com.example.datingWebsite.model.UserRole;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserResponse register(UserRequest request) {

        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email already exist!");
        }

        Users user = new Users();

        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername(request.username());

        Users savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.isActive(),
                savedUser.getCreatedAt(),
                savedUser.getUsername()
        );
    }

    public String verify(UserRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(request.username());
        } else {
            return "fail";
        }
    }
}