package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ServerExceptionHandler {

    // Обработка NotFoundException
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", "The required object was not found.",
                "Ресурс не найден; " + ex.getMessage(), LocalDateTime.now());
    }

    // Обработка RequestNotFoundException
    @ExceptionHandler(RequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestNotFoundException(RequestNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", "The required object was not found.",
                "Запрос не найден; " + ex.getMessage(), LocalDateTime.now());
    }

    // Обработка конфликтов данных
    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictException(DataConflictException ex) {
        return new ErrorResponse("CONFLICT", "Integrity constraint has been violated.",
                "could not execute statement; " + ex.getMessage(), LocalDateTime.now());
    }

    // Обработка валидации параметров
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterNotValid(final ParameterNotValidException ex) {
        return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.",
                ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        return new ErrorResponse("BAD_REQUEST", "Validation failed for argument.",
                ex.getMessage(), LocalDateTime.now());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException ex) {
        return new ErrorResponse("CONFLICT", "Integrity constraint has been violated.",
                "could not execute statement; ", LocalDateTime.now());
    }

}
