package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.service.EmailService;
import com.luka.gamesellerrating.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserManagementFacade {

    private final KeycloakService keycloakService;
    private final EmailService emailService;

    public void createUser(UserDTO userDTO) {
        keycloakService.userCreate(userDTO);
        emailService.sendUserVerificationEmail(userDTO.getEmail());
    }

    public void updateUser(UserDTO userDTO) {
        keycloakService.userUpdate(userDTO);
    }

    public void verifyUserEmail(String email, String token) {
        keycloakService.verifyUserEmail(email, token);
    }
}
