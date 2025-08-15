package ru.practicum.exception;

import lombok.Getter;

@Getter
public class NewConstraintViolationException extends RuntimeException {
    private final String status;
    private final String reason;

    public NewConstraintViolationException(String message, String status, String reason) {
        super(message);
        this.reason = reason;
        this.status = status;
    }
}
