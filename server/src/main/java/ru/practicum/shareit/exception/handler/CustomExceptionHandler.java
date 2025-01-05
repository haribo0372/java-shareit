package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserEmailIsNotUnique;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.model.ErrorResponse;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Запрашиваемый ресурс не найден", ex.getMessage());
        loggingErrorResponse(errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handle(MissingRequestHeaderException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Неверный запрос", ex.getMessage());
        loggingErrorResponse(errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handle(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse("В доступе отказано", ex.getMessage());
        loggingErrorResponse(errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handle(ValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации", ex.getMessage());
        loggingErrorResponse(errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserEmailIsNotUnique.class)
    public ResponseEntity<ErrorResponse> handle(UserEmailIsNotUnique ex) {
        ErrorResponse errorResponse = new ErrorResponse("Запрос не может быть выполнен", ex.getMessage());
        loggingErrorResponse(errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Произошла непредвиденная ошибка", ex.getClass() + " " + ex.getMessage());
        log.trace(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void loggingErrorResponse(ErrorResponse errorResponse) {
        log.warn("Ошибка: {}", errorResponse);
    }
}