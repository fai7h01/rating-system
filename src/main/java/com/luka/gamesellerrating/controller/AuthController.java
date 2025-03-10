package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.UserDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> registerUser(@RequestBody UserDTO user) {
        UserDTO registeredUser = userService.save(user);
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("User is successfully registered.")
                .data(registeredUser).build());
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> activateUserAccount(@RequestParam("email") String email,
                                                               @RequestParam("token") String token) {
        userService.verifyEmail(email, token);
        return noContent().build();
    }
}
