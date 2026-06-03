package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.AuthResponseDto;
import com.activitytracker.backend.dto.LoginRequestDto;
import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public UserService(UserRepository userRepository, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    public UUID registerUser(UserRegistrationDto dto) {
        UUID keycloakId = keycloakService.createUserInKeycloak(dto);

        try {
            User user = new User();
            user.setUserId(keycloakId);
            userRepository.save(user);
        } catch (Exception e) {
            keycloakService.deleteUserFromKeycloak(keycloakId);
            throw new RuntimeException("Database-failure: Registration canceled. Please try again.");
        }
        return keycloakId;
    }

    public UUID loginUser(LoginRequestDto dto) {
        UUID keycloakId = keycloakService.verifyCredentialsAndGetId(dto.getEmail(), dto.getPassword());

        return keycloakId;
    }
}