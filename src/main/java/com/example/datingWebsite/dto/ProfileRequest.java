package com.example.datingWebsite.dto;

import com.example.datingWebsite.model.ProfileGender;
import jakarta.validation.constraints.*;

public record ProfileRequest(
        @NotEmpty(message = "First name can't be empty")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotEmpty(message = "Last name can't be empty")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotNull(message = "Age can't be null")
        @Min(value = 17, message = "Age must be at least 18")
        @Max(value = 120, message = "Age must be at most 100")
        Integer age,

        @NotEmpty(message = "Gender can't be empty")
        ProfileGender gender,

        @NotEmpty(message = "City can't be empty")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        String city,

        @NotBlank(message = "Bio can't be empty")
        @Size(min = 10, max = 500, message = "Bio must be between 10 and 500 characters")
        String bio
) {
}
