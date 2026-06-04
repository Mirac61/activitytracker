package com.activitytracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String userId;
    private String accessToken;
    private String refreshToken;
}