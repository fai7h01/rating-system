package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.ResetPasswordDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
import com.luka.gamesellerrating.exception.UserAlreadyExistsException;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.mapper.UserMapper;
import com.luka.gamesellerrating.repository.UserRepository;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.service.helper.RatingStatsUpdater;
import com.luka.gamesellerrating.service.helper.UserManagementFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserManagementFacade userManagementFacade;
    private final UserMapper userMapper;
    private final RatingStatsUpdater ratingStatsUpdater;

    @Override
    public List<UserDTO> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO user) {
        validateUserCreate(user);
        var userEntity = userMapper.toEntity(user);
        var savedEntity = userRepository.save(userEntity);
        userManagementFacade.createUser(user);
        return userMapper.toDto(savedEntity);
    }

    @Override
    public void updateOverallRating(Long id) {
        var foundUser = findUserEntityById(id);
        ratingStatsUpdater.updateUserRating(foundUser);
        userRepository.save(foundUser);
    }

    @Override
    public UserDTO findById(Long id) {
        var foundUser = findUserEntityById(id);
        return userMapper.toDto(foundUser);
    }

    @Override
    public UserDTO findByEmail(String email) {
        var foundUser = findUserEntityByEmail(email);
        return userMapper.toDto(foundUser);
    }

    @Override
    public List<UserDTO> findSellersByUsernameContaining(String username) {
        var sellers = userRepository.findAllByUsernameContainingAndRole(username, Role.SELLER);
        return userMapper.toDtoList(sellers);
    }

    @Override
    public List<UserDTO> findAllSellers(Sort sort) {
        return userMapper.toDtoList(userRepository.findAllByRole(Role.SELLER, sort));
    }

    @Override
    public List<UserDTO> findTopSellers(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        return userMapper.toDtoList(userRepository.findTopSellers(Role.SELLER, pageable));
    }

    @Override
    public void updateStatus(Long id, UserStatus status) {
        var foundUser = findUserEntityById(id);
        foundUser.setStatus(status);
        userRepository.save(foundUser);
    }

    @Override
    @Transactional
    public void verifyEmail(String email, String token) {
        var foundUser = findUserEntityByEmail(email);
        foundUser.setEmailVerified(true);
        userRepository.save(foundUser);
        userManagementFacade.verifyUserEmail(email, token);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String token, ResetPasswordDTO newPassword) {
        var foundUser = findUserEntityByEmail(email);
        foundUser.setPassword(newPassword.getNewPassword());
        foundUser.setConfirmPassword(newPassword.getConfirmPassword());
        userManagementFacade.updateUser(userMapper.toDto(foundUser));
    }

    private void validateUserCreate(UserDTO user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use.");
        }
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken.");
        }
    }

    private User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private User findUserEntityByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
