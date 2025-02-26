package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.UserDTO;

public interface UserService {

    UserDTO save(UserDTO user);
    void delete(Long id);
    void verifyUser(Long id);
    void rejectUser(Long id);
    void suspendUser(Long id);

}
