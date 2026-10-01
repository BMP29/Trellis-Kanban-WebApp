package com.example.auth_server.service;

import com.example.auth_server.dto.ChangePasswordRequest;
import com.example.auth_server.dto.ChangeUsernameRequest;
import com.example.auth_server.dto.UserRegistrationRequest;
import com.example.auth_server.dto.VerifyUserRequestDto;
import com.example.auth_server.enums.VerificationCodeType;

import java.util.UUID;

public interface IAuthService {
    void register(UserRegistrationRequest data);
    void verify(VerifyUserRequestDto data);

    void sendVerificationEmail(String username, VerificationCodeType type);

    void updatePassword(UUID userId, ChangePasswordRequest request);
    void updateUsername(String username, ChangeUsernameRequest request);
    void updateEmail(UUID userId, String newEmail);

}
