package com.activitytracker.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // User error
    @ExceptionHandler(InvalidActivityException.class)
    public ResponseEntity<String> handleInvalidActivity(InvalidActivityException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid argument: " + e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("Fehlerhaftes JSON-Format");
    }

    // System error
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleServerError(RuntimeException e) {
        log.error("Registrierung fehlgeschlagen: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError().body("Ein unerwarteter Fehler ist aufgetreten.");
    }
}