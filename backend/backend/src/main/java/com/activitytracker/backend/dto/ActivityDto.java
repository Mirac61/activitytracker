package com.activitytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


public record ActivityDto (
    @NotBlank
    UUID id,
    @NotBlank
    String activityName,
    @NotNull
    LocalDate activityDate,
    @NotBlank
    UUID userId,
    @NotNull
    OffsetDateTime createdAt)
{}