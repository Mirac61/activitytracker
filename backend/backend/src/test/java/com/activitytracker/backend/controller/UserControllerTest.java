package com.activitytracker.backend.controller;

import com.activitytracker.backend.service.KeycloakService;
import com.activitytracker.backend.service.WeatherService;
import com.activitytracker.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "keycloak.serverUrl=http://localhost:8081",
                "keycloak.adminRealm=master",
                "keycloak.clientId=admin-cli",
                "keycloak.googleExchangeClientId=dummy-client-id",
                "keycloak.adminUser=admin",
                "keycloak.adminPassword=admin",
                "keycloak.realm=ActivityTracker"
        })
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private KeycloakService keycloakService;

    @MockitoBean
    private WeatherService weatherService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private AccessTokenResponse mockTokenResponse;

    @BeforeEach
    void setUp() {
        userRepository.deleteAllInBatch();

        mockTokenResponse = new AccessTokenResponse();
        mockTokenResponse.setToken("mock-access-token");
        mockTokenResponse.setRefreshToken("mock-refresh-token");
    }

    @Test
    void register() throws Exception {
        Mockito.when(keycloakService.createUserInKeycloak(any())).thenReturn(TEST_USER_ID);

        String json = """
                {
                    "vorname": "Max",
                    "nachname": "Mustermann",
                    "email": "max@test.de",
                    "password": "securePassword123"
                }
                """;

        mvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()));
    }

    @Test
    void login() throws Exception {
        KeycloakService.AuthenticationResult mockResult =
                new KeycloakService.AuthenticationResult(TEST_USER_ID.toString(), mockTokenResponse, "TestUser", "test@test.de");

        Mockito.when(keycloakService.authenticateUser(anyString(), anyString())).thenReturn(mockResult);

        String json = """
                {
                    "email": "max@test.de",
                    "password": "securePassword123"
                }
                """;

        mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    void refresh() throws Exception {
        KeycloakService.AuthenticationResult mockResult =
                new KeycloakService.AuthenticationResult(TEST_USER_ID.toString(), mockTokenResponse, "TestUser", "test@test.de");

        Mockito.when(keycloakService.refreshTokens(anyString())).thenReturn(mockResult);

        String json = """
                {
                    "refreshToken": "existing-refresh-token"
                }
                """;

        mvc.perform(post("/api/users/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));
    }

    @Test
    void loginWithGoogle() throws Exception {
        KeycloakService.AuthenticationResult mockResult =
                new KeycloakService.AuthenticationResult(TEST_USER_ID.toString(), mockTokenResponse, "TestUser", "test@test.de");

        Mockito.when(keycloakService.authenticateWithGoogle(anyString())).thenReturn(mockResult);

        String json = """
                {
                    "idToken": "google-id-token"
                }
                """;

        mvc.perform(post("/api/users/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()));
    }

    @Test
    void invalidRegisterEmptyFields() throws Exception {
        String json = """
                {
                    "vorname": "",
                    "nachname": "",
                    "email": "",
                    "password": ""
                }
                """;

        mvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}