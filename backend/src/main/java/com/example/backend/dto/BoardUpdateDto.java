package com.example.backend.dto;

import java.util.UUID;

public record BoardUpdateDto(UUID boardId, String title) {
}
