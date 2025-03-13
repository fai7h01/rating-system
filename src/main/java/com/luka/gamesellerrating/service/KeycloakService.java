package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;

public interface KeycloakService {

    void userCreate(UserDTO dto);
    void userUpdate(UserDTO dto);
    void verifyUserEmail(String email, String token);

}
