package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.*;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.exception.UserRegistrationException;
import com.activitytracker.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Transactional
    public UUID registerUser(UserRegistrationDto dto) {

        UUID keycloakId = keycloakService.createUserInKeycloak(dto);

            User user = new User();
            user.setUserId(keycloakId);
            userRepository.save(user);

            return keycloakId;
    }

    public LoginResponseDto loginUser(LoginRequestDto dto) {
        KeycloakService.AuthenticationResult authResult = keycloakService.authenticateUser(dto.getEmail(), dto.getPassword());

        return new LoginResponseDto(
                authResult.userId(),
                authResult.tokenResponse().getToken(),
                authResult.tokenResponse().getRefreshToken()
        );
    }

    public LoginResponseDto refreshUserToken(RefreshRequestDto dto) {
        KeycloakService.AuthenticationResult refreshResult = keycloakService.refreshTokens(dto.getRefreshToken());

        return new LoginResponseDto(
                refreshResult.userId(),
                refreshResult.tokenResponse().getToken(),
                refreshResult.tokenResponse().getRefreshToken()
        );
    }
}