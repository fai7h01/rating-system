package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;

public interface UserService {

    UserDTO save(UserDTO user);
    UserDTO findById(Long id);
}
