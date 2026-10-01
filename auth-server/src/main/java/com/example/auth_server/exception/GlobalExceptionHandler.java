package com.example.auth_server.exception;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return handleExceptionInternal(
                ex,
                new ApiError("Error on validation", errors),
                headers,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex,
            WebRequest request
    ) {
        return handleExceptionInternal(
                ex,
                new ApiError(
                        "User not found",
                        List.of(ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<Object> handleInvalidVerificationCodeException(
            VerificationCodeNotFoundException ex,
            WebRequest request
    ) {
        return handleExceptionInternal(
                ex,
                new ApiError(
                        "Invalid verification code",
                        List.of(ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler({
            IncorrectPasswordException.class,
            NewPasswordMatchesCurrentPasswordException.class
    })
    public ResponseEntity<Object> handlePasswordException(
            RuntimeException ex,
            WebRequest request
    ) {
        return handleExceptionInternal(
                ex,
                new ApiError(
                        "Password change failed",
                        List.of(ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(UsernameAlreadyInUseException.class)
    public ResponseEntity<Object> handleUsernameAlreadyInUseException(
            UsernameAlreadyInUseException ex,
            WebRequest request
    ) {
        return handleExceptionInternal(
                ex,
                new ApiError(
                        "Invalid username",
                        List.of(ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }


}
