package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.UserRegistrationDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;
import java.util.Collections;

@Service
public class KeycloakService {

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

    public String createUserInKeycloak(UserRegistrationDto dto) {

        //Set up Keycloak
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(adminUser)
                .password(adminPassword)
                .build();

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
        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            String path = response.getLocation().getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } else {
            throw new RuntimeException("Keycloak Fehler: " + response.getStatusInfo().getReasonPhrase());
        }
    }
}