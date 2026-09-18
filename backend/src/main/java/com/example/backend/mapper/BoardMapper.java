package com.example.backend.mapper;

import com.example.backend.dto.BoardColumnDto;
import com.example.backend.dto.BoardDto;
import com.example.backend.dto.TaskDto;
import com.example.backend.entity.Board;
import com.example.backend.entity.BoardColumn;
import com.example.backend.entity.Task;
import java.util.ArrayList;
import java.util.Comparator;

public class BoardMapper {

    public static BoardDto toDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getColumns().stream()
                        .sorted(Comparator.comparingInt(BoardColumn::getPosition))
                        .map(BoardMapper::toColumnDto)
                        .toList()
        );
    }

    public static BoardColumnDto toColumnDto(BoardColumn column) {
        return new BoardColumnDto(
                column.getId(),
                column.getTitle(),
                column.getPosition(),
                new ArrayList<>(
                        column.getTasks().stream()
                                .sorted(Comparator.comparingInt(Task::getPosition))
                                .map(BoardMapper::toTaskDto)
                                .toList()
                )
        );
    }

    public static TaskDto toTaskDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getColumn().getId(),
                task.getPosition(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getCategories(),
                task.getColorCode()
        );
    }

}
