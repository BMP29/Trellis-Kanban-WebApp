package com.example.auth_server.exception;

public class NewPasswordMatchesCurrentPasswordException extends RuntimeException {

    public NewPasswordMatchesCurrentPasswordException() {
        super("New password must not be the same as the current password");
    }

    public NewPasswordMatchesCurrentPasswordException(String message) {
        super(message);
    }
}

