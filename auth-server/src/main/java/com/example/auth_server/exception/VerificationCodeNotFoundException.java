package com.example.auth_server.exception;

public class VerificationCodeNotFoundException extends RuntimeException {

    public VerificationCodeNotFoundException() {
        super("Verification code not found.");
    }

    public VerificationCodeNotFoundException(String message) {
        super(message);
    }
}
