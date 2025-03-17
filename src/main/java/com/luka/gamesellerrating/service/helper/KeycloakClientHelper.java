package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.config.KeycloakProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class KeycloakClientHelper {

    protected final KeycloakProperties keycloakProperties;

    public Keycloak getKeycloakInstance() {
        return Keycloak.getInstance(keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getMasterRealm(), keycloakProperties.getMasterUser()
                , keycloakProperties.getMasterUserPswd(), keycloakProperties.getMasterClient());
    }

}
