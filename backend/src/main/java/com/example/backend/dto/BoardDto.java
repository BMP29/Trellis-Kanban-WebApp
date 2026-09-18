package com.example.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record BoardDto(UUID id, String title, List<BoardColumnDto> columns) {}