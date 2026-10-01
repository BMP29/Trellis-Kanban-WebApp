package com.example.auth_server.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }

    public EmailAlreadyInUseException() {
        super("Email is already in use.");
    }
}
