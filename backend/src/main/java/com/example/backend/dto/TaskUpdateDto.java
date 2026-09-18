package com.example.backend.dto;

import com.example.backend.enums.TaskPriority;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TaskUpdateDto(
                            UUID taskId,
                            UUID newColumnId,
                            Integer position,
                            String title,
                            String description,
                            LocalDate dueDate,
                            TaskPriority priority,
                            List<String> categories,
                            String colorCode) {
}
