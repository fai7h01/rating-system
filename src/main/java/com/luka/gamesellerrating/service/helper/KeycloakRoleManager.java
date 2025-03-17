package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.config.KeycloakProperties;
import com.luka.gamesellerrating.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KeycloakRoleManager extends KeycloakClientHelper {

    public KeycloakRoleManager(KeycloakProperties keycloakProperties) {
        super(keycloakProperties);
    }

    public void updateClientRoles(RealmResource realmResource, String userId, Role role) {
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
}
