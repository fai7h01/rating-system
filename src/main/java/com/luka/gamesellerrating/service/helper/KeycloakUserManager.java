package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.config.KeycloakProperties;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.exception.KeycloakUserCreateException;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KeycloakUserManager extends KeycloakClientHelper {

    public KeycloakUserManager(KeycloakProperties keycloakProperties) {
        super(keycloakProperties);
    }

    public Response createUser(RealmResource realm, UserRepresentation userRepresentation) {
        Response response = realm.users().create(userRepresentation);
        handleResponse(response);
        return response;
    }

    public void handleResponse(Response response) {
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

    public void activateUser(String email) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            UsersResource usersResource = keycloak.realm(keycloakProperties.getRealm()).users();
            UserRepresentation user = findUserByEmail(usersResource, email);
            user.setEmailVerified(true);
            usersResource.get(user.getId()).update(user);
        }
    }

    public void updateUserFields(UserRepresentation user, UserDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
    }

    public void updatePasswordIfNeeded(UsersResource users, String userId, String password) {
        if (password != null && !password.isEmpty()) {
            updatePassword(users, userId, password);
        }
    }

    public void updatePassword(UsersResource usersResource, String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setTemporary(false);
        credential.setValue(newPassword);
        usersResource.get(userId).resetPassword(credential);
    }

    public UserRepresentation findUserByEmail(UsersResource usersResource, String email) {
        List<UserRepresentation> users = usersResource.search(null, null, null, email, 0, 1);
        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found.");
        }
        return users.get(0);
    }

    public UserRepresentation getUserRepresentation(UserDTO dto) {
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
}
