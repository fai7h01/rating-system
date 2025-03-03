package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.exception.UserAlreadyExistsException;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.repository.UserRepository;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public UserDTO save(UserDTO user) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use.");
        }
        if (userRepository.existsByUsernameIgnoreCase(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken.");
        }
        var userEntity = mapperUtil.convert(user, new User());
        var savedEntity = userRepository.save(userEntity);
        return mapperUtil.convert(savedEntity, new UserDTO());
    }

    @Override
    public UserDTO findById(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found."));
        return mapperUtil.convert(foundUser, new UserDTO());
    }

}
