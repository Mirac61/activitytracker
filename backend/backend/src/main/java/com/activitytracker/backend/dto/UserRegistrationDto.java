package com.activitytracker.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    private String vorname;
    private String nachname;
    private String email;
    private String password;
}