package com.activitytracker.backend.controller;

import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.repository.UserRepository;
import com.activitytracker.backend.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            UUID keycloakId = keycloakService.createUserInKeycloak(registrationDto);
            User userEntity = new User(keycloakId);
            userRepository.save(userEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registrierung erfolgreich!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fehler: " + e.getMessage());
        }
    }
}