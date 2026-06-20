package com.activitytracker.backend.dto;

public record GoogleLoginResponseDto(
    String userId,
    String accessToken,
    String refreshToken
) {}