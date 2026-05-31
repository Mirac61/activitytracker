package com.activitytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
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