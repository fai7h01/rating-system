package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.ResetPasswordDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.enums.UserStatus;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();
    UserDTO save(UserDTO user);
    UserDTO findById(Long id);
    UserDTO findByEmail(String email);
    List<UserDTO> findSellersByUsernameContaining(String username);
    List<UserDTO> findAllSellers(Sort sort);
    void updateStatus(Long id, UserStatus status);
    void verifyEmail(String email, String token);
    void resetPassword(String email, String token, ResetPasswordDTO newPassword);
    void updateOverallRating(Long id);
}
