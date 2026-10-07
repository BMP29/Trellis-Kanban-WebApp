package com.example.auth_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeEmailRequest(
        @NotBlank(message = "Email must not be empty")
        @Email(message = "Email must be valid")
        @Size(max = 64, message = "Email must not exceed 64 characters")
        String newEmail,

        @NotBlank(message = "Code must not be empty")
        @Size(min = 6, max = 6, message = "Code must be exactly 6 characters")
        String code
) {
}