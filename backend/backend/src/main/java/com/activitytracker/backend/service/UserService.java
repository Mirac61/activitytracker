package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.*;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.exception.GoogleAuthenticationException;
import com.activitytracker.backend.exception.UserRegistrationException;
import com.activitytracker.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
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

            return keycloakId;

        } catch (Exception e) {
            log.error("Failed to save user to local database after Keycloak creation. Triggering Rollback... Error: {}", e.getMessage());

            try {
                keycloakService.deleteUserFromKeycloak(keycloakId);
                log.info("Rollback successful: User {} removed from Keycloak to preserve data consistency.", keycloakId);
            } catch (Exception rollbackException) {
                log.error("CRITICAL: Rollback failed! Could not delete user {} from Keycloak: {}", keycloakId, rollbackException.getMessage());
            }

            throw new UserRegistrationException("Registrierung fehlgeschlagen. Interner Datenbankfehler.", e);
        }
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

    public GoogleLoginResponseDto loginUserWithGoogle(GoogleLoginRequestDto dto) {
        KeycloakService.AuthenticationResult authResult = keycloakService.authenticateWithGoogle(dto.idToken());
        UUID userId = UUID.fromString(authResult.userId());

        try {
            userRepository.findById(userId).ifPresentOrElse(
                    user -> log.info("Google user login processed. User already existed in local database."),
                    () -> {
                        log.info("New Google user detected. Synchronizing new profile to local database.");
                        User newUser = new User();
                        newUser.setUserId(userId);
                        userRepository.save(newUser);
                    }
            );
        } catch (DataAccessException e) {
            log.error("Database connectivity failure during Google user synchronization");
            throw new GoogleAuthenticationException("Interner Datenbankfehler bei der Google-Anmeldung.", e);
        }

        return new GoogleLoginResponseDto(
                authResult.userId(),
                authResult.tokenResponse().getToken(),
                authResult.tokenResponse().getRefreshToken()
        );
    }
}