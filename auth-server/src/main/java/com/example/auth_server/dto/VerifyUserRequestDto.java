package com.example.auth_server.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyUserRequestDto(
        @NotBlank(message = "Code must not be empty")
        String verificationCode
) {
}