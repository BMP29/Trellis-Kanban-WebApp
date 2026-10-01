package com.example.auth_server.exception;

import java.util.List;

public record ApiError(
        String message,
        List<String> errors
) {
}