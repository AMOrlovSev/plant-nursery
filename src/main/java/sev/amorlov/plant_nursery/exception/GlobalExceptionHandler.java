package sev.amorlov.plant_nursery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Хэндлер для 404 Not Found (Когда сущность не найдена в БД)
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(jakarta.persistence.EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Not Found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    // 2. Хэндлер для 401 Unauthorized (Наш новый AuthException для неверного пароля/логина)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401

        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Unauthorized",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    // 3. Хэндлер для 400 Bad Request (Для общих некорректных аргументов, если они где-то остались)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Bad Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        HttpStatus status = HttpStatus.BAD_REQUEST;

        var error = new ErrorResponse(LocalDateTime.now(), status.value(), "Validation Failed", errorMessage);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var error = new ErrorResponse(LocalDateTime.now(), status.value(), "Insufficient Stock", ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        var error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Conflict / Race Condition",
                "Товар был изменен или куплен другим пользователем. Пожалуйста, обновите данные и попробуйте снова."
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var error = new ErrorResponse(LocalDateTime.now(), status.value(), "Illegal Business State", ex.getMessage());
        return new ResponseEntity<>(error, status);
    }
}