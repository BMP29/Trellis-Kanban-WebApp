package com.example.auth_server.service;

import com.example.auth_server.dto.UserDto;
import com.example.auth_server.entity.User;

import java.util.UUID;

public interface IUserService {
    public UserDto getUser();
    public UserDto updateUser();
}
