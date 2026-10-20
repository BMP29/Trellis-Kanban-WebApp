package com.example.auth_server.service;

import com.example.auth_server.dto.UserDto;

public interface IUserService {
    public UserDto getUser(String username);
    public UserDto updateUser();
}
