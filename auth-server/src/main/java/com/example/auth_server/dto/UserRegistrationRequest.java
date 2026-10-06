package com.example.auth_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest (
        @Size(min = 8)
        @NotBlank(message = "Username must not be empty")
        String username,
        @Size(min = 8, max = 64, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                message = "Password must contain at least one uppercase letter, " +
                        "one lowercase letter, one number, and one special character"
        )
        @NotBlank(message = "Password must no be empty")
        String password,
        @Email(message = "E-mail must be valid")
        @NotBlank(message = "E-mail must not be empty")
        String email
) {
}
