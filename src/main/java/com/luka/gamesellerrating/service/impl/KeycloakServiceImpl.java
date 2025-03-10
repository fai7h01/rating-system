package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.config.KeycloakProperties;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.TokenType;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.TokenService;
import com.luka.gamesellerrating.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.KeycloakAuthenticationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final UserService userService;
    private final TokenService tokenService;

    public KeycloakServiceImpl(KeycloakProperties keycloakProperties, UserService userService, TokenService tokenService) {
        this.keycloakProperties = keycloakProperties;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public void userCreate(UserDTO dto) {

        UserRepresentation keycloakUser = getUserRepresentation(dto);

        try (Keycloak keycloak = getKeycloakInstance()){
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            Response response = realmResource.users().create(keycloakUser);

            if (response.getStatus() == 201) {
               assignClientRole(realmResource, getCreatedId(response), dto.getRole());
            }

        } catch (Exception e) {
            log.error("Failed to register user in keycloak: {}", e.getMessage());
            throw new KeycloakAuthenticationException("User registration failed", e);
        }
    }

    @Override
    public UserDTO getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            log.info("jwt: {}", jwt.getTokenValue());
            String username = jwt.getClaimAsString("email");
            if (username != null) {
                return userService.findByEmail(username);
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }

    @Override
    public boolean isUserAnonymous() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority())))
                .orElse(true);
    }

    @Override
    public void verifyUserEmail(String email, String token) {
        tokenService.validateToken(email, token, TokenType.VERIFICATION);
        activateUser(email);
    }

    private void activateUser(String email) {
        UsersResource usersResource = getUserResource();
        List<UserRepresentation> users = usersResource.search(null, null, null, email, 0, 1);
        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found to verify email.");
        }
        UserRepresentation userRepresentation = users.get(0);
        userRepresentation.setEmailVerified(true);
        usersResource.get(userRepresentation.getId()).update(userRepresentation);
    }


    private UsersResource getUserResource() {
        Keycloak keycloak = getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
        return realmResource.users();
    }

    private void assignClientRole(RealmResource realmResource, String userId, Role role) {
        ClientRepresentation appClient = realmResource.clients()
                .findByClientId(keycloakProperties.getClientId()).get(0);

        RoleRepresentation userClientRole = realmResource.clients()
                .get(appClient.getId()).roles().get(role.getValue()).toRepresentation();

        realmResource.users().get(userId).roles().clientLevel(appClient.getId())
                .add(Collections.singletonList(userClientRole));
    }

    private UserRepresentation getUserRepresentation(UserDTO dto) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(dto.getPassword());

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(dto.getUsername());
        keycloakUser.setFirstName(dto.getFirstName());
        keycloakUser.setLastName(dto.getLastName());
        keycloakUser.setEmail(dto.getEmail());
        keycloakUser.setCredentials(List.of(credential));
        keycloakUser.setEmailVerified(false);
        keycloakUser.setEnabled(true);
        return keycloakUser;
    }

    private Keycloak getKeycloakInstance() {
        return Keycloak.getInstance(keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getMasterRealm(), keycloakProperties.getMasterUser()
                , keycloakProperties.getMasterUserPswd(), keycloakProperties.getMasterClient());
    }
}
