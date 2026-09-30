package com.example.auth_server.repository;

import com.example.auth_server.entity.User;
import com.example.auth_server.entity.VerificationCode;
import com.example.auth_server.enums.VerificationCodeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    boolean existsByUserAndType(
            User user,
            VerificationCodeType type
    );
}
