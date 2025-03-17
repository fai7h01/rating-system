package com.luka.gamesellerrating.exception;

import com.luka.gamesellerrating.dto.wrapper.ExceptionWrapper;
import com.luka.gamesellerrating.dto.wrapper.ValidationExceptionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.ResponseEntity.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Throwable.class, Exception.class, RuntimeException.class})
    public ResponseEntity<ExceptionWrapper> handleGenericExceptions(Throwable exception) {
        log.error(exception.getMessage());
        return status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionWrapper.builder()
                        .success(false)
                        .message("Action failed: An error occurred!")
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler({UserAlreadyExistsException.class, AnonymousUserAlreadyExistsException.class,
            RatingAlreadyExistsException.class, GameObjectAlreadyExistsException.class, InvalidTokenException.class})
    public ResponseEntity<ExceptionWrapper> handleConflictExceptions(Throwable exception) {
        log.error(exception.getMessage());
        return status(HttpStatus.CONFLICT)
                .body(ExceptionWrapper.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .httpStatus(HttpStatus.CONFLICT)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler({RatingAccessDeniedException.class, GameObjectAccessDeniedException.class})
    public ResponseEntity<ExceptionWrapper> handleAccessDeniedException(RatingAccessDeniedException exception) {
        log.error(exception.getMessage());
        return status(HttpStatus.FORBIDDEN)
                .body(ExceptionWrapper.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .httpStatus(HttpStatus.FORBIDDEN)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler({UserNotFoundException.class, RatingNotFoundException.class, GameObjectNotFoundException.class})
    public ResponseEntity<ExceptionWrapper> handleNotFoundExceptions(Throwable exception) {
        log.error(exception.getMessage());
        return status(HttpStatus.NOT_FOUND)
                .body(ExceptionWrapper.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(KeycloakUserCreateException.class)
    public ResponseEntity<ExceptionWrapper> handleKeycloakUserCreateException(KeycloakUserCreateException exception) {
        log.error(exception.getMessage());
        return status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionWrapper.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionWrapper> handleValidationExceptions(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());
        ExceptionWrapper exceptionWrapper = ExceptionWrapper.builder()
                .success(false)
                .message("Invalid Input(s)")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();

        List<ValidationExceptionWrapper> validationExceptions = collectValidationExceptions(exception);

        exceptionWrapper.setValidationExceptions(validationExceptions);
        exceptionWrapper.setErrorCount(validationExceptions.size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionWrapper);

    }

    private List<ValidationExceptionWrapper> collectValidationExceptions(MethodArgumentNotValidException exception) {

        List<ValidationExceptionWrapper> validationExceptions = new ArrayList<>();

        for (ObjectError error : exception.getBindingResult().getAllErrors()) {

            String fieldName = ((FieldError) error).getField();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            String errorMessage = error.getDefaultMessage();

            ValidationExceptionWrapper validationException = ValidationExceptionWrapper.builder()
                    .errorField(fieldName)
                    .rejectedValue(rejectedValue)
                    .reason(errorMessage)
                    .build();

            validationExceptions.add(validationException);

        }

        return validationExceptions;

    }


}
