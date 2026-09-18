package com.example.backend.exception;

import java.util.List;

public record ApiError(
        String message,
        List<String> errors
) {
}
