package ru.practicum.exception;

public record ApiError(String status, String reason, String message, String timestamp) {
}
