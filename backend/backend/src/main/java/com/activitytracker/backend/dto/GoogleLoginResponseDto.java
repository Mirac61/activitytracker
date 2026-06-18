package com.activitytracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GoogleLoginResponseDto {
    private String userId;
    private String accessToken;
    private String refreshToken;
}