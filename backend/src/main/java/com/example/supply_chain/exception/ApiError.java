package com.example.supply_chain.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * Represents a standardized API error payload returned by the global exception handler.
 */
public class ApiError {
    private final OffsetDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<String> details;

    public ApiError(HttpStatus status, String message, String path, List<String> details) {
        this.timestamp = OffsetDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}


