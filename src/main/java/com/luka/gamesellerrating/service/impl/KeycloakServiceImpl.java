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
            Response response = createUser(realmResource, keycloakUser);
            handleResponse(response);
            updateClientRoles(keycloak.realm(keycloakProperties.getRealm()), getCreatedId(response), dto.getRole());
        }
    }

    @Override
    public void userUpdate(UserDTO dto) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            RealmResource realm = keycloak.realm(keycloakProperties.getRealm());
            UserRepresentation user = findUserByEmail(realm.users(), dto.getEmail());
            updateUserFields(user, dto);
            updateClientRoles(realm, user.getId(), dto.getRole());
            updatePasswordIfNeeded(realm.users(), user.getId(), dto.getPassword());
            realm.users().get(user.getId()).update(user);
        }
    }

    @Override
    public void verifyUserEmail(String email, String token) {
        tokenService.validateToken(email, token, VERIFICATION_TOKEN);
        activateUser(email);
    }

    private void updateClientRoles(RealmResource realmResource, String userId, Role role) {
        String clientId = findClientId(realmResource);
        clearExistingRoles(realmResource, userId, clientId);
        assignRole(realmResource, userId, clientId, role.getValue());
        log.debug("Updated client role for user {}: {}", userId, role.getValue());
    }

    private String findClientId(RealmResource realmResource) {
        List<ClientRepresentation> clients = realmResource.clients()
                .findByClientId(keycloakProperties.getClientId());
        if (clients.isEmpty()) {
            throw new IllegalStateException("Client not found: " + keycloakProperties.getClientId());
        }
        return clients.get(0).getId();
    }

    private void clearExistingRoles(RealmResource realmResource, String userId, String clientId) {
        List<RoleRepresentation> existingRoles = realmResource.users().get(userId)
                .roles().clientLevel(clientId).listEffective();
        if (!existingRoles.isEmpty()) {
            realmResource.users().get(userId).roles()
                    .clientLevel(clientId).remove(existingRoles);
        }
    }

    private void assignRole(RealmResource realmResource, String userId, String clientId, String roleName) {
        RoleRepresentation userClientRole = realmResource.clients()
                .get(clientId).roles().get(roleName).toRepresentation();
        realmResource.users().get(userId).roles()
                .clientLevel(clientId).add(List.of(userClientRole));
    }

    private void activateUser(String email) {
        UsersResource usersResource = getUserResource();
        UserRepresentation user = findUserByEmail(usersResource, email);
        user.setEmailVerified(true);
        usersResource.get(user.getId()).update(user);
    }

    private UserRepresentation findUserByEmail(UsersResource usersResource, String email) {
        List<UserRepresentation> users = usersResource.search(null, null, null, email, 0, 1);
        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found.");
        }
        return users.get(0);
    }

    private UsersResource getUserResource() {
        try (Keycloak keycloak = getKeycloakInstance()) {
            RealmResource realmResource = keycloak.realm(keycloakProperties.getRealm());
            return realmResource.users();
        }
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

    private Response createUser(RealmResource realm, UserRepresentation userRepresentation) {
        Response response = realm.users().create(userRepresentation);
        handleResponse(response);
        return response;
    }

    private void handleResponse(Response response) {
        int status = response.getStatus();
        if (status == 201) {
            log.info("User created successfully.");
        } else if (status == 409) {
            log.error("User already exists (status 409).");
            throw new KeycloakUserCreateException("User already exists.");
        } else {
            log.error("Unexpected status: {}", status);
            throw new KeycloakUserCreateException("Failed with status: " + status);
        }
    }

    private void updateUserFields(UserRepresentation user, UserDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
    }

    private void updatePasswordIfNeeded(UsersResource users, String userId, String password) {
        if (password != null && !password.isEmpty()) {
            updatePassword(users, userId, password);
        }
    }

    private void updatePassword(UsersResource usersResource, String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(newPassword);
        usersResource.get(userId).resetPassword(credential);
    }
}
