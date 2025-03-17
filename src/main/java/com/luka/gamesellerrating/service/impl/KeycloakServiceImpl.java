package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.config.KeycloakProperties;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.exception.KeycloakUserCreateException;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.TokenService;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.luka.gamesellerrating.enums.TokenType.VERIFICATION_TOKEN;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakProperties keycloakProperties;
    private final TokenService tokenService;

    public KeycloakServiceImpl(KeycloakProperties keycloakProperties, TokenService tokenService) {
        this.keycloakProperties = keycloakProperties;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public void userCreate(UserDTO dto) {

        UserRepresentation keycloakUser = getUserRepresentation(dto);

        try (Keycloak keycloak = getKeycloakInstance()) {
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            Response response = realmResource.users().create(keycloakUser);

            if (response.getStatus() == 201) {
                assignClientRole(realmResource, getCreatedId(response), dto.getRole());
            }

        } catch (Exception e) {
            log.error("Failed to register user in keycloak: {}", e.getMessage());
            throw new KeycloakUserCreateException("Failed to register user in keycloak.");
        }
    }

    @Override
    public void userUpdate(UserDTO dto) {

        try (Keycloak keycloak = getKeycloakInstance()) {

            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> userRepresentations = usersResource.search(dto.getUsername());

            if (userRepresentations.isEmpty()) {
                throw new UserNotFoundException("User does not exist.");
            }

            UserRepresentation keycloakUser = userRepresentations.get(0);

            updateRoles(realmResource, keycloakUser.getId(), dto.getRole().getValue());

            keycloakUser.setFirstName(dto.getFirstName());
            keycloakUser.setLastName(dto.getLastName());

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                updatePassword(usersResource, keycloakUser.getId(), dto.getPassword());
            }

            usersResource.get(keycloakUser.getId()).update(keycloakUser);
        }
    }

    private void updateRoles(RealmResource realmResource, String userId, String role) {

        ClientRepresentation appClient = realmResource.clients()
                .findByClientId(keycloakProperties.getClientId()).get(0);

        String clientId = appClient.getId();

        List<RoleRepresentation> existingRoles = realmResource.users().get(userId)
                .roles().clientLevel(clientId).listEffective();
        existingRoles.forEach(existingRole -> realmResource.users().get(userId)
                .roles().clientLevel(clientId).remove(Collections.singletonList(existingRole)));

        RoleRepresentation userClientRole = realmResource.clients().get(clientId)
                .roles().get(role).toRepresentation();

        realmResource.users().get(userId).roles().clientLevel(clientId)
                .add(Collections.singletonList(userClientRole));

    }

    private void updatePassword(UsersResource usersResource, String userId, String newPassword) {

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(newPassword);

        usersResource.get(userId).resetPassword(credential);
    }

    @Override
    public void verifyUserEmail(String email, String token) {
        tokenService.validateToken(email, token, VERIFICATION_TOKEN);
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
