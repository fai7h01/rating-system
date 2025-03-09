package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.enums.UserStatus;
import com.luka.gamesellerrating.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper> findAllUsers() {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(userService.findAll())
                .build());
    }


    @PutMapping("/users/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam("status") UserStatus status) {
        userService.updateStatus(id, status);
        return noContent().build();
    }


}
