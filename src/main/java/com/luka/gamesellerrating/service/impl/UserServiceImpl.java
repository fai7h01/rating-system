package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
import com.luka.gamesellerrating.exception.UserAlreadyExistsException;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.repository.UserRepository;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, @Lazy KeycloakService keycloakService, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .toList();
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO user) {
        validateNewUser(user);
        keycloakService.userCreate(user);
        var userEntity = mapperUtil.convert(user, new User());
        var savedEntity = userRepository.save(userEntity);
        return mapperUtil.convert(savedEntity, new UserDTO());
    }

    @Override
    public UserDTO findById(Long id) {
        var foundUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));
        return mapperUtil.convert(foundUser, new UserDTO());
    }

    @Override
    public UserDTO findByUsername(String username) {
        var foundUser = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UserNotFoundException("User not found."));
        return mapperUtil.convert(foundUser, new UserDTO());
    }

    @Override
    public List<UserDTO> findSellersByUsernameContaining(String username) {
        return userRepository.findAllByUsernameContainingAndRole(username, Role.Seller).stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .toList();
    }

    @Override
    public List<UserDTO> findAllSellers() {
        return userRepository.findAllByRole(Role.Seller).stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .toList();
    }

    public void updateStatus(Long id, UserStatus status) {
        var foundUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        foundUser.setStatus(status);
        userRepository.save(foundUser);
    }

    private void validateNewUser(UserDTO user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use.");
        }
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken.");
        }
    }
}
