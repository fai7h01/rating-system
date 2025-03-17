package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.config.KeycloakProperties;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.TokenService;
import com.luka.gamesellerrating.service.helper.KeycloakRoleManager;
import com.luka.gamesellerrating.service.helper.KeycloakUserManager;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.luka.gamesellerrating.enums.TokenType.VERIFICATION_TOKEN;
import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakUserManager userManager;
    private final KeycloakRoleManager roleManager;
    private final TokenService tokenService;

    public KeycloakServiceImpl(KeycloakUserManager userManager, KeycloakRoleManager roleManager, TokenService tokenService) {
        this.userManager = userManager;
        this.roleManager = roleManager;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public void userCreate(UserDTO dto) {
        UserRepresentation keycloakUser = userManager.getUserRepresentation(dto);
        try (Keycloak keycloak = userManager.getKeycloakInstance()) {
            RealmResource realmResource = keycloak.realm(getKeycloakProperties().getRealm());
            Response response = userManager.createUser(realmResource, keycloakUser);
            roleManager.updateClientRoles(keycloak.realm(getKeycloakProperties().getRealm()), getCreatedId(response), dto.getRole());
        }
    }

    @Override
    public void userUpdate(UserDTO dto) {
        try (Keycloak keycloak = userManager.getKeycloakInstance()) {
            RealmResource realm = keycloak.realm(getKeycloakProperties().getRealm());
            UserRepresentation user = userManager.findUserByEmail(realm.users(), dto.getEmail());
            userManager.updateUserFields(user, dto);
            roleManager.updateClientRoles(realm, user.getId(), dto.getRole());
            userManager.updatePasswordIfNeeded(realm.users(), user.getId(), dto.getPassword());
            realm.users().get(user.getId()).update(user);
        }
    }

    @Override
    public void verifyUserEmail(String email, String token) {
        tokenService.validateToken(email, token, VERIFICATION_TOKEN);
        userManager.activateUser(email);
    }

    private KeycloakProperties getKeycloakProperties() {
        return userManager.getKeycloakProperties();
    }
}
