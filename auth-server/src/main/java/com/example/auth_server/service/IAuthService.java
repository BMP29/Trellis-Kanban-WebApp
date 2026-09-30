package com.example.auth_server.service;

import com.example.auth_server.dto.ChangePasswordRequest;
import com.example.auth_server.dto.UserRegistrationRequest;
import com.example.auth_server.dto.VerifyUserRequestDto;
import com.example.auth_server.entity.User;
import com.example.auth_server.enums.VerificationCodeType;

import java.util.UUID;

public interface IAuthService {
    void register(UserRegistrationRequest data);
    void verify(VerifyUserRequestDto data);

    public void sendVerificationEmail(User user, VerificationCodeType type);

    public void updatePassword(UUID userId, ChangePasswordRequest request);
    public void updateUsername(UUID userId, String newUsername);

    void sendDeletionCode(User user);

    public void updateEmail(UUID userId, String newEmail);

}
