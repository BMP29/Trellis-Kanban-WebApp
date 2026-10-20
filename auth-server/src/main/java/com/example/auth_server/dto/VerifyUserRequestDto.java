package com.example.auth_server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyUserRequestDto(
        @Email(message = "E-mail must be valid")
        @NotBlank(message = "E-mail must not be empty")
        String email,
        @NotBlank(message = "Code must not be empty")
        String verificationCode
) {
}