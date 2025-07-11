package ru.practicum.exception;

public class ParameterNotValidException extends IllegalArgumentException {
    private final String parameter;
    private final String reason;

    public ParameterNotValidException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }
}

