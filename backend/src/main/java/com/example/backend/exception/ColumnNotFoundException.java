package com.example.backend.exception;

import java.util.UUID;

public class ColumnNotFoundException extends RuntimeException {
    public ColumnNotFoundException(UUID columnId) {
        super("Column " + columnId + " not found");
    }
}
