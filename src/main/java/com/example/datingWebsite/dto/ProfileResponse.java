package com.example.datingWebsite.dto;

import com.example.datingWebsite.model.ProfileGender;
import jakarta.validation.constraints.*;

public record ProfileResponse(
        Long id,
        String firstName,
        String lastName,
        Integer age,
        ProfileGender gender,
        String city,
        String bio
) {
}
