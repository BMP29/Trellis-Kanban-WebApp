package com.example.auth_server.controller;

import com.example.auth_server.dto.*;
import com.example.auth_server.entity.User;
import com.example.auth_server.enums.VerificationCodeType;
import com.example.auth_server.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserRegistrationRequest data) {
        this.authService.register(data);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody VerifyUserRequestDto request) {
        this.authService.verify(
                jwt.getSubject(),
                request.verificationCode()
        );

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/password/request")
    public void requestDeletion(@AuthenticationPrincipal User user) {
        authService.sendDeletionCode(user);
    }

    @PostMapping("/username/request")
    public ResponseEntity<Void> requestUsernameChange(@AuthenticationPrincipal Jwt jwt) {
        authService.sendVerificationEmail(
                jwt.getSubject(),
                VerificationCodeType.USERNAME_CHANGE
        );

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request
    ) {
        authService.updatePassword(user.getId(), request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/username")
    public ResponseEntity<Void> updateUsername(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangeUsernameRequest request
            ) {
        authService.updateUsername(jwt.getSubject(), request);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
