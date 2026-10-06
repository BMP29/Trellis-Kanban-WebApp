package com.example.auth_server.service;

import com.example.auth_server.dto.ChangePasswordRequest;
import com.example.auth_server.dto.ChangeUsernameRequest;
import com.example.auth_server.dto.UserRegistrationRequest;
import com.example.auth_server.dto.VerifyUserRequestDto;
import com.example.auth_server.enums.VerificationCodeType;
import jakarta.validation.Valid;

import java.util.UUID;

public interface IAuthService {
    void register(@Valid UserRegistrationRequest data);
    void verify(String username, String verificationCode);

    void sendVerificationEmail(String username, VerificationCodeType type);

    void updatePassword(UUID userId, ChangePasswordRequest request);
    void updateUsername(String username, ChangeUsernameRequest request);
    void updateEmail(UUID userId, String newEmail);

}
