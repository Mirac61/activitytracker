package com.activitytracker.backend.service;

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

    public void registerUser(UserRegistrationDto dto) {
        // Create User
        String keycloakId = keycloakService.createUserInKeycloak(dto);

        // Save user
        User user = new User();
        user.setUserId(UUID.fromString(keycloakId));

        userRepository.save(user);
    }
}