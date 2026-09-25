package com.example.datingWebsite.dto;

import com.example.datingWebsite.model.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        UserRole role,
        boolean isActive,
        LocalDateTime createdAt,
        String username
) {}