package com.activitytracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    public String userId;
    public String accessToken;
    public String refreshToken;
}