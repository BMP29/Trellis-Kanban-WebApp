package com.example.auth_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest (
        @Size(min = 8)
        @NotBlank(message = "Username must not be empty")
        String username,
        @NotBlank(message = "Password must no be empty")
        String password,
        @Email(message = "E-mail must be valid")
        @NotBlank(message = "E-mail must not be empty")
        String email
) {
}
