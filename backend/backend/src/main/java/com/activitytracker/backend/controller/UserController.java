package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.AuthResponseDto;
import com.activitytracker.backend.dto.LoginRequestDto;
import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        UUID userId = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDto(userId.toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        UUID userId = userService.loginUser(dto);
        return ResponseEntity.ok(new AuthResponseDto(userId.toString()));
    }
}   