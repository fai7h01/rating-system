package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.ResetPasswordDTO;
import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.EmailService;
import com.luka.gamesellerrating.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> registerUser(@RequestBody UserDTO user) {
        return status(HttpStatus.CREATED).body(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.CREATED.value())
                .message("User is successfully registered.")
                .data(userService.save(user)).build());
    }

    @PostMapping("/confirmation")
    public ResponseEntity<Void> activateUserAccount(@RequestParam("email") String email,
                                                    @RequestParam("token") String token) {
        userService.verifyEmail(email, token);
        return noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> sendResetPasswordEmail(@RequestParam("email") String email) {
        emailService.sendPasswordResetEmail(email);
        return noContent().build();
    }


    @PostMapping("/new-password")
    public ResponseEntity<Void> resetUserPassword(@RequestParam("email") String email, @RequestParam("token") String token,
                                                  @RequestBody ResetPasswordDTO resetPassword) {
        userService.resetPassword(email, token, resetPassword);
        return noContent().build();
    }
}
