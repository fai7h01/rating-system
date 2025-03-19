package com.luka.gamesellerrating.unit.controller;

import com.luka.gamesellerrating.controller.AuthController;
import com.luka.gamesellerrating.dto.ResetPasswordDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.EmailService;
import com.luka.gamesellerrating.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthController authController;

    private UserDTO userDTO;
    private ResetPasswordDTO resetPasswordDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("password");

        resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setNewPassword("newPassword");
        resetPasswordDTO.setConfirmPassword("newPassword");
    }

    @Test
    void testRegisterUser() {
        when(userService.save(any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<ResponseWrapper> response = authController.registerUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getCode());
        assertEquals("User is successfully registered.", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());
    }

    @Test
    void testActivateUserAccount() {
        doNothing().when(userService).verifyEmail(anyString(), anyString());

        ResponseEntity<Void> response = authController.activateUserAccount("john.doe@example.com", "dummyToken");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testSendResetPasswordEmail() {
        doNothing().when(emailService).sendPasswordResetEmail(anyString());

        ResponseEntity<Void> response = authController.sendResetPasswordEmail("john.doe@example.com");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testResetUserPassword() {
        doNothing().when(userService).resetPassword(anyString(), anyString(), any(ResetPasswordDTO.class));

        ResponseEntity<Void> response = authController.resetUserPassword("john.doe@example.com", "dummyToken", resetPasswordDTO);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}