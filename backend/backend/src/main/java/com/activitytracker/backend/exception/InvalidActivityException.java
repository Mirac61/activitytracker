package com.activitytracker.backend.exception;

public class InvalidActivityException extends RuntimeException {
    public InvalidActivityException(String message) {
        super(message);
    }
}