package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class KeycloakService {

    private Keycloak keycloak;

    @Value("${keycloak.serverUrl}")
    private String serverUrl;
    @Value("${keycloak.adminRealm}")
    private String adminRealm;
    @Value("${keycloak.clientId}")
    private String clientId;
    @Value("${keycloak.adminUser}")
    private String adminUser;
    @Value("${keycloak.adminPassword}")
    private String adminPassword;
    @Value("${keycloak.realm}")
    private String targetRealm;


    @jakarta.annotation.PostConstruct
    public void initKeycloak() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(adminUser)
                .password(adminPassword)
                .build();
    }

    public UUID createUserInKeycloak(UserRegistrationDto dto) {

        //Set user information
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getVorname());
        user.setLastName(dto.getNachname());

        //set password information
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(dto.getPassword());
        user.setCredentials(Collections.singletonList(passwordCred));

        //Create user
        UsersResource usersResource = keycloak.realm(targetRealm).users();

        // Try-with-resources makes sure, that response.close() is always called
        try (Response response = usersResource.create(user)) {

            if (response.getStatus() == 201) {
                String path = response.getLocation().getPath();
                String stringId = path.substring(path.lastIndexOf("/") + 1);
                log.info("User successfully created in Keycloak. ID: {}", stringId);
                return UUID.fromString(stringId);
            } else if (response.getStatus() == 409) {
                throw new UserAlreadyExistsException("User with this email already exists");
            } else {
                String errorReason = response.getStatusInfo().getReasonPhrase();
                log.error("Keycloak error while creating: {}", errorReason);
                throw new RuntimeException("Keycloak error: " + errorReason);
            }
        }
    }

    public void deleteUserFromKeycloak(UUID userId) {
        keycloak.realm(targetRealm).users().get(userId.toString()).remove();
    }

    public UUID verifyCredentialsAndGetId(String email, String password) {
        UserRepresentation user = keycloak.realm(targetRealm)
                .users()
                .searchByEmail(email, true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new jakarta.ws.rs.NotAuthorizedException("E-Mail oder Passwort falsch."));

        try (Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(targetRealm)
                .clientId(clientId)
                .username(user.getUsername())
                .password(password)
                .build()) {

            userKeycloak.tokenManager().getAccessTokenString();
        }

        return UUID.fromString(user.getId());
    }
}