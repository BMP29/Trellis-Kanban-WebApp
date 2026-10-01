package com.example.auth_server.dto;

public record ChangeUsernameRequest(
        String username,
        String code
) {
}
