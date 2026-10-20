package com.example.auth_server.service.impl;

import com.example.auth_server.dto.UserDto;
import com.example.auth_server.entity.User;
import com.example.auth_server.exception.UserNotFoundException;
import com.example.auth_server.mapper.UserMapper;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private UserRepository userRepository;
    private EmailServiceImpl emailService;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUser() {
        return null;
    }
}
