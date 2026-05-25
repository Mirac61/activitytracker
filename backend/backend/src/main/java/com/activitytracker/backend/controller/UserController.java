package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDto dto) {
        userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registrierung erfolgreich!");
    }
}