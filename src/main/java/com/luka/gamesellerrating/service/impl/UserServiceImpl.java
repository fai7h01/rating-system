package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.exception.UserAlreadyExistsException;
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
        Optional<User> foundUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (foundUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        var userEntity = mapperUtil.convert(user, new User());
        var savedEntity = userRepository.save(userEntity);
        return mapperUtil.convert(savedEntity, new UserDTO());
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void verifyUser(Long id) {

    }

    @Override
    public void rejectUser(Long id) {

    }

    @Override
    public void suspendUser(Long id) {

    }
}
