package com.example.auth_server.exception;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super("The password is incorrect.");
    }

    public IncorrectPasswordException(String message) {
        super(message);
    }
}
