package com.activitytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
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