package com.activitytracker.backend.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String vorname;
    private String nachname;
    private String email;
    private String password;
}