package ru.practicum.exception;

public class NewBadRequestException extends RuntimeException {
  public NewBadRequestException(String message) {
    super(message);
  }
}
