package com.example.auth_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest(
        @Size(min = 8, max = 64)
        @NotBlank(message = "Username must not be empty")
        String newUsername,
        @Size(min = 6, max = 6, message = "Code must be exactly 6 characters")
        @NotBlank(message = "Code must not be empty")
        String code
) {
}
