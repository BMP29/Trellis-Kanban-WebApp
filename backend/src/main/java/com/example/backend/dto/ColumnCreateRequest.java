package com.example.backend.dto;

import java.util.UUID;

public record ColumnCreateRequest(
        UUID boardId,
        String columnName
) {
}
