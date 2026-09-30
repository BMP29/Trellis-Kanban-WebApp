package com.example.auth_server.dto;

public record VerifyUserRequestDto(
        String email,
        String verificationCode
) {
}