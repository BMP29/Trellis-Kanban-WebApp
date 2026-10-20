package com.example.auth_server.service.impl;

import com.example.auth_server.dto.UserDto;
import com.example.auth_server.entity.User;
import com.example.auth_server.exception.ExpiredVerificationCodeException;
import com.example.auth_server.repository.UserRepository;
import com.example.auth_server.service.IUserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Objects;


@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    private UserRepository userRepository;
    private EmailServiceImpl emailService;

    @Override
    public UserDto getUser() {
        return null;
    }

    @Override
    public UserDto updateUser() {
        return null;
    }
}
