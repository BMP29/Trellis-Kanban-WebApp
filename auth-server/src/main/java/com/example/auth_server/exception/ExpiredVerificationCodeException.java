package com.example.auth_server.exception;

public class ExpiredVerificationCodeException extends RuntimeException {
    public ExpiredVerificationCodeException(String message) {
        super(message);
    }

    public ExpiredVerificationCodeException() {
        super("Verification code has expired. Please request a new one.");
    }
}
