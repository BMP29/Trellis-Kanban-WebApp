package com.example.auth_server.dto;

import java.time.Instant;

public record UserDto(
        String username,
        String email,
        String pfpPath,
        Instant createdAt,
        Instant lastUpdateAt
) {
}
