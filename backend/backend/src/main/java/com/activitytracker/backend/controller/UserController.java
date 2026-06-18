package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.*;
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
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        LoginResponseDto response = userService.loginUser(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        LoginResponseDto response = userService.refreshUserToken(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<GoogleLoginResponseDto> loginWithGoogle(@Valid @RequestBody GoogleLoginRequestDto dto) {
        GoogleLoginResponseDto response = userService.loginUserWithGoogle(dto);
        return ResponseEntity.ok(response);
    }
}   