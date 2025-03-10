package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.UserStatus;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();
    UserDTO save(UserDTO user);
    UserDTO findById(Long id);
    UserDTO findByUsername(String username);
    List<UserDTO> findSellersByUsernameContaining(String username);
    List<UserDTO> findAllSellers();
    void updateStatus(Long id, UserStatus status);
}
