package com.luka.gamesellerrating.exception;

import com.luka.gamesellerrating.dto.wrapper.ExceptionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.ResponseEntity.status;

import java.time.LocalDateTime;

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

    @ExceptionHandler({UserAlreadyExistsException.class})
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

    @ExceptionHandler({UserNotFoundException.class})
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

}
