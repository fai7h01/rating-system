package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;

public interface AuthenticationService {

    UserDTO getLoggedInUser();
    boolean isUserAnonymous();
}
