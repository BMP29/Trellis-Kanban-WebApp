package com.example.backend.mapper;

import com.example.backend.dto.ColumnUpdateDto;
import com.example.backend.entity.BoardColumn;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ColumnMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "board", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateColumnFromDto(ColumnUpdateDto dto, @MappingTarget BoardColumn entity);
}
