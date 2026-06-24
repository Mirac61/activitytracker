package com.activitytracker.backend;

import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.repository.UserRepository;
import com.activitytracker.backend.service.FriendService;
import com.activitytracker.backend.service.KeycloakService;
import com.activitytracker.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// TODO: Error
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private FriendService friendService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterUser_Success() {
        // 1. Arrange
        UserRegistrationDto dto = new UserRegistrationDto("Max", "Mustermann", "max@test.de", "password123");
        UUID fakeKeycloakId = UUID.randomUUID();

        // simulate keycloak with fake UUID
        when(keycloakService.createUserInKeycloak(dto)).thenReturn(fakeKeycloakId);

        when(friendService.generateFriendCode("Max")).thenReturn("Max-AAAAAAAA");

        // 2. Act
        userService.registerUser(dto);

        // 3. Assert
        verify(keycloakService, times(1)).createUserInKeycloak(dto);
        verify(userRepository, times(1)).save(any(User.class));
        verify(keycloakService, never()).deleteUserFromKeycloak(any(UUID.class));
    }

    @Test
    public void testRegisterUser_DatabaseFails_ShouldRollbackKeycloak() {
        // 1. Arrange
        UserRegistrationDto dto = new UserRegistrationDto("Max", "Mustermann", "max@test.de", "password123");
        UUID fakeKeycloakId = UUID.randomUUID();

        when(keycloakService.createUserInKeycloak(dto)).thenReturn(fakeKeycloakId);

        when(friendService.generateFriendCode("Max")).thenReturn("Max-AAAAAAAA");

        // Simulate database error
        doThrow(new RuntimeException("DB Error")).when(userRepository).save(any(User.class));

        // 2. Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerUser(dto));

        verify(keycloakService, times(1)).deleteUserFromKeycloak(fakeKeycloakId);
    }
}