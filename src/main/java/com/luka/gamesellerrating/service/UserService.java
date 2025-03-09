package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO save(UserDTO user);
    UserDTO findById(Long id);
    UserDTO findByUsername(String username);
    UserDTO findSellerByUsername(String username);
    List<UserDTO> findAllSellers();
}
