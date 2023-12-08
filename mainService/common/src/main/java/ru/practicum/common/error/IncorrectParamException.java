package ru.practicum.common.error;

public class IncorrectParamException extends RuntimeException {
    public IncorrectParamException(String message) {
        super(message);
    }
}