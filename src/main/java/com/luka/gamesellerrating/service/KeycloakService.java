package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;

public interface KeycloakService {

    void userCreate(UserDTO dto);
    UserDTO getLoggedInUser();
    boolean isUserAnonymous();


}
