package com.example.backend.dto;

import java.util.ArrayList;
import java.util.UUID;

public record BoardColumnDto(
        UUID id,
        String tittle,
        int position,
        ArrayList<TaskDto> tasks
) {
}
