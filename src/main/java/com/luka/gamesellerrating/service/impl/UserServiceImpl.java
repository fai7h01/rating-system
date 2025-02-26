package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.repository.UserRepository;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

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
        return null;
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
