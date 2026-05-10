package com.activitytracker.backend;

import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.entity.User;
import com.activitytracker.backend.repository.UserRepository;
import com.activitytracker.backend.service.KeycloakService;
import com.activitytracker.backend.service.UserService; // Den brauchen wir jetzt!
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterUser_Success() {
        //Arrange data
        UserRegistrationDto dto = new UserRegistrationDto("Max", "Mustermann", "max@test.de", "password123");
        String fakeKeycloakId = UUID.randomUUID().toString();

        //simulate Keycloak
        when(keycloakService.createUserInKeycloak(dto)).thenReturn(fakeKeycloakId);

        //call userservice to register the user
        userService.registerUser(dto);

        // 3. Assert
        //Test to see if the user service called the keycloak service
        verify(keycloakService, times(1)).createUserInKeycloak(dto);

        //Test to see if the user service called the repository to save the user
        verify(userRepository, times(1)).save(any(User.class));
    }
}