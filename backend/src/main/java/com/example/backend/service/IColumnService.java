package com.example.backend.service;

import com.example.backend.dto.BoardColumnDto;
import com.example.backend.dto.ColumnCreateRequest;
import com.example.backend.dto.ColumnUpdateDto;

import java.util.UUID;

public interface IColumnService {
    public BoardColumnDto createColumn(ColumnCreateRequest data);
    public ColumnUpdateDto updateColumn(UUID boardId, UUID columnId, ColumnUpdateDto data);
    public void deleteColumn(UUID columnId);
}
