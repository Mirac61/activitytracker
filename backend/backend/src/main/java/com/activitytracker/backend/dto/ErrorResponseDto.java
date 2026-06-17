package com.activitytracker.backend.dto;

public record ErrorResponseDto(
        int status,
        String message) {
}