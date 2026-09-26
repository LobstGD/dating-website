package com.example.datingWebsite.mapper;

import com.example.datingWebsite.dto.UserRequest;
import com.example.datingWebsite.dto.UserResponse;
import com.example.datingWebsite.model.Profile;
import com.example.datingWebsite.model.UserRole;
import com.example.datingWebsite.model.Users;
import com.example.datingWebsite.repository.ProfileRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserMapper {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public Users toEntity(UserRequest request) {

        Users user = new Users();

        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    public UserResponse toResponse(Users user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
