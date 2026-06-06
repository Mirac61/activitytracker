package com.activitytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class ActivityDto {
    @NotBlank
    private String id;
    @NotBlank
    private String activityName;
    @NotNull
    private LocalDate activityDate;
    @NotBlank
    private String userId;
    @NotNull
    private OffsetDateTime createdAt;
}