package com.activitytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ActivityDto(
        @NotBlank
        String activityName,

        @NotNull
        LocalDate activityDate,

        @NotNull
        UUID userId,

        @NotNull
        OffsetDateTime createdAt) {
}