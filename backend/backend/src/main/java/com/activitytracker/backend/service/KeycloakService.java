package com.activitytracker.backend.service;

import com.activitytracker.backend.dto.UserRegistrationDto;
import com.activitytracker.backend.exception.GoogleAuthenticationException;
import com.activitytracker.backend.exception.InvalidCredentialsException;
import com.activitytracker.backend.exception.UserAlreadyExistsException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.NotAuthorizedException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class KeycloakService {

    private Keycloak keycloak;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.serverUrl}")
    private String serverUrl;
    @Value("${keycloak.adminRealm}")
    private String adminRealm;
    @Value("${keycloak.clientId}")
    private String clientId;
    @Value("${keycloak.googleExchangeClientId}")
    private String googleExchangeClientId;
    @Value("${keycloak.clientSecret:#{null}}")
    private String clientSecret;
    @Value("${keycloak.adminUser}")
    private String adminUser;
    @Value("${keycloak.adminPassword}")
    private String adminPassword;
    @Value("${keycloak.realm}")
    private String targetRealm;


    @PostConstruct
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
        UserRepresentation user = getUserRepresentation(dto);
        UsersResource usersResource = keycloak.realm(targetRealm).users();

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == 201) {
                String path = response.getLocation().getPath();
                String stringId = path.substring(path.lastIndexOf("/") + 1);
                log.info("User successfully created in Keycloak. ID: {}", stringId);
                return UUID.fromString(stringId);
            } else if (response.getStatus() == 409) {
                throw new UserAlreadyExistsException("Nutzer mit dieser Email existiert bereits");
            } else {
                String errorReason = response.getStatusInfo().getReasonPhrase();
                log.error("Keycloak error while creating: {}", errorReason);
                throw new RuntimeException("Keycloak error: " + errorReason);
            }
        }
    }

    private static @NonNull UserRepresentation getUserRepresentation(UserRegistrationDto dto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getVorname());
        user.setLastName(dto.getNachname());

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(dto.getPassword());
        user.setCredentials(Collections.singletonList(passwordCred));
        return user;
    }

    public void deleteUserFromKeycloak(UUID userId) {
        keycloak.realm(targetRealm).users().get(userId.toString()).remove();
    }

    public AuthenticationResult authenticateUser(String email, String password) {
        UserRepresentation user = keycloak.realm(targetRealm)
                .users()
                .searchByEmail(email, true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new InvalidCredentialsException("E-Mail oder Passwort falsch."));

        try (Keycloak userKeycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(targetRealm)
                .clientId(clientId)
                .username(user.getUsername())
                .password(password)
                .scope("openid offline_access")
                .build()) {

            AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();

            if (tokenResponse == null) {
                throw new NotAuthorizedException("Authentifizierung fehlgeschlagen.");
            }

            return new AuthenticationResult(user.getId(), tokenResponse, user.getUsername(), user.getEmail());

        } catch (jakarta.ws.rs.NotAuthorizedException e) {
            log.warn("Failed to login (wrong credentials) for: {}", email);
            throw new InvalidCredentialsException("E-Mail oder Passwort falsch.");
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Critical system failure at Keycloak-Login for {}: ", email, e);
            throw new RuntimeException("Keycloak service unavailable", e);
        }
    }

    public AuthenticationResult refreshTokens(String refreshToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "refresh_token");
            map.add("client_id", clientId);
            map.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            String tokenEndpoint = serverUrl + "/realms/" + targetRealm + "/protocol/openid-connect/token";

            AccessTokenResponse tokenResponse = restTemplate.postForObject(tokenEndpoint, request, AccessTokenResponse.class);

            if (tokenResponse == null) {
                throw new NotAuthorizedException("Token-Refresh fehlgeschlagen.");
            }

            AccessToken decryptedToken = TokenVerifier.create(tokenResponse.getToken(), AccessToken.class).getToken();
            String verifiedUserId = decryptedToken.getSubject();

            return new AuthenticationResult(verifiedUserId, tokenResponse, null, null);

        } catch (Exception e) {
            log.error("Error during token refresh via RestTemplate: {}", e.getMessage());
            throw new InvalidCredentialsException("Deine Sitzung ist abgelaufen. Bitte melde dich erneut an.");
        }
    }

    public AuthenticationResult authenticateWithGoogle(String googleIdToken) {
        log.info("[Google-Auth] Starting cryptographic token verification...");

        GoogleIdToken.Payload payload;
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList("741626481823-gceju18n97rqdd5l16pcea00s6cttk2l.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleIdToken);
            if (idToken == null) {
                log.warn("[Google-Auth] Token verification failed: Verifier returned null.");
                throw new GoogleAuthenticationException("Das bereitgestellte Google-Token ist ungültig oder abgelaufen.");
            }
            payload = idToken.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            log.error("[Google-Auth] Cryptographic verification failed due to internal security/I-O error");
            throw new GoogleAuthenticationException("Kryptographische Verifizierung des Google-Tokens fehlgeschlagen.", e);
        }

        String email = payload.getEmail();
        var usersResource = keycloak.realm(targetRealm).users();
        var searchResults = usersResource.searchByEmail(email, true);

        UserRepresentation user;

        try {
            if (searchResults.isEmpty()) {
                log.info("[Google-Auth] Unregistered identity detected. Creating new Keycloak account...");

                user = new UserRepresentation();
                user.setEnabled(true);
                user.setUsername(email);
                user.setEmail(email);
                user.setFirstName((String) payload.get("given_name"));
                user.setLastName((String) payload.get("family_name"));

                try (Response response = usersResource.create(user)) {
                    if (response.getStatus() != 201) {
                        log.error("[Google-Auth] Keycloak account creation rejected with HTTP status: {}", response.getStatus());
                        throw new GoogleAuthenticationException("Automatisches Anlegen des Nutzers in Keycloak fehlgeschlagen.");
                    }

                    String path = response.getLocation().getPath();
                    String newId = path.substring(path.lastIndexOf("/") + 1);
                    user.setId(newId);
                    log.info("[Google-Auth] Keycloak account successfully provisioned.");
                }
            } else {
                user = searchResults.get(0);
                log.info("[Google-Auth] Existing Keycloak account resolved.");
            }

            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            if (tokenResponse == null) {
                log.error("[Google-Auth] Token manager failed to issue access token response.");
                throw new GoogleAuthenticationException("Es konnte kein AccessToken generiert werden.");
            }
            String googleUsername = payload.get("name") != null ? (String) payload.get("name") : email;
            return new AuthenticationResult(user.getId(), tokenResponse, googleUsername, email);

        } catch (WebApplicationException e) {
            log.error("[Google-Auth] Keycloak REST API communication failure during provisioning");
            throw new GoogleAuthenticationException("Fehler bei der Kommunikation mit dem Identity-Management-Server.", e);
        }
    }

    public record AuthenticationResult(String userId, AccessTokenResponse tokenResponse, String username, String email) {
    }
}