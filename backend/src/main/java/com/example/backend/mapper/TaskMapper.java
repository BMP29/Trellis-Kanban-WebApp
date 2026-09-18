package com.example.backend.mapper;

import com.example.backend.dto.*;
import com.example.backend.entity.BoardColumn;
import com.example.backend.entity.Task;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "column", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateTaskFromDto(TaskUpdateDto dto, @MappingTarget Task entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "column", source = "column")
    @Mapping(target = "position", source = "position")
    Task toEntity(
            TaskCreateRequest request,
            UUID id,
            BoardColumn column,
            int position
    );

    TaskCreatedResponseDto toCreatedResponse(Task task);

    @Mapping(target = "columnId", source = "column.id")
    TaskUpdateResponseDto toUpdatedResponse(Task task);
}
