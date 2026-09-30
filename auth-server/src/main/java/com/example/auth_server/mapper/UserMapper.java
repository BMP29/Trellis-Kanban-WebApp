package com.example.auth_server.mapper;

import com.example.auth_server.dto.UserDto;
import com.example.auth_server.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}

