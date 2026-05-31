package com.activitytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    @NotBlank
    private String vorname;
    @NotBlank
    private String nachname;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}