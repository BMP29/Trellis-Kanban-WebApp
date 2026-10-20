package com.example.auth_server.controller;

import com.example.auth_server.dto.UserDto;
import com.example.auth_server.mapper.UserMapper;
import com.example.auth_server.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private UserServiceImpl userService;
    private UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.getUser(jwt.getSubject())
        );
    }
}
