package com.activitytracker.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidActivityException.class)
    public ResponseEntity<String> handleInvalidActivity(InvalidActivityException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Ungültige UUID oder fehlerhafte Eingabe");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body("Fehlerhaftes JSON-Format oder ungültiger Datumswert");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception e) {
        return ResponseEntity.internalServerError().body("Ein unerwarteter Fehler ist aufgetreten");
    }
}