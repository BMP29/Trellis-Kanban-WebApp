package com.example.backend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errors.add(
                    fieldError.getField() + ": " + fieldError.getDefaultMessage()
            );
        });

        return handleExceptionInternal(
                ex,
                new ApiError("Error on validation", errors),
                headers,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(BoardNotFoundException.class)
    protected ResponseEntity<Object> handleBoardNotFoundException(
            Exception ex,
            WebRequest request) {

        return handleExceptionInternal(
                ex,
                new ApiError(
                        "Invalid Arguments",
                        List.of(ex.getMessage())
                ),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }
}
