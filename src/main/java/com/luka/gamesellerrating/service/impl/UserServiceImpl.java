package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.ResetPasswordDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
import com.luka.gamesellerrating.exception.UserAlreadyExistsException;
import com.luka.gamesellerrating.exception.UserNotFoundException;
import com.luka.gamesellerrating.repository.UserRepository;
import com.luka.gamesellerrating.service.EmailService;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.service.helper.RatingStatsUpdater;
import com.luka.gamesellerrating.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final EmailService emailService;
    private final RatingStatsUpdater ratingStatsUpdater;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, @Lazy KeycloakService keycloakService, @Lazy EmailService emailService,
                           RatingStatsUpdater ratingStatsUpdater, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
        this.emailService = emailService;
        this.ratingStatsUpdater = ratingStatsUpdater;
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
        validateUser(user);
        var userEntity = mapperUtil.convert(user, new User());
        var savedEntity = userRepository.save(userEntity);
        keycloakService.userCreate(user);
        emailService.sendUserVerificationEmail(savedEntity.getEmail());
        return mapperUtil.convert(savedEntity, new UserDTO());
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
        return mapperUtil.convert(foundUser, new UserDTO());
    }

    @Override
    public UserDTO findByEmail(String email) {
        var foundUser = findUserEntityByEmail(email);
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
        return userRepository.findAllByRole(Role.Seller, Sort.by("overallRating")).stream()
                .map(user -> mapperUtil.convert(user, new UserDTO()))
                .toList();
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
        keycloakService.verifyUserEmail(email, token);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String token, ResetPasswordDTO newPassword) {
        var foundUser = findUserEntityByEmail(email);
        foundUser.setPassword(newPassword.getNewPassword());
        var savedUser = userRepository.save(foundUser);
        keycloakService.userUpdate(mapperUtil.convert(savedUser, new UserDTO()));
    }

    private void validateUser(UserDTO user) {
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
