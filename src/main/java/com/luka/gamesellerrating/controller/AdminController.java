package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.enums.UserStatus;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.service.RatingService;
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
    private final AnonymousUserService anonymousUserService;
    private final RatingService ratingService;

    public AdminController(UserService userService, AnonymousUserService anonymousUserService, RatingService ratingService) {
        this.userService = userService;
        this.anonymousUserService = anonymousUserService;
        this.ratingService = ratingService;
    }

    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper> findAllUser() {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Users retrieved successfully")
                .data(userService.findAll())
                .build());
    }

    @GetMapping("/users/anonymous")
    public ResponseEntity<ResponseWrapper> findAllAnonymousUser() {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Anonymous users retrieved successfully")
                .data(anonymousUserService.findAll())
                .build());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable("id") Long id,
                                                 @RequestParam("status") UserStatus status) {
        userService.updateStatus(id, status);
        return noContent().build();
    }

    @PutMapping("/ratings/{id}/status")
    public ResponseEntity<Void> updateRatingStatus(@PathVariable("id") Long id,
                                                   @RequestParam("status") RatingStatus status) {
        ratingService.updateStatus(id, status);
        return noContent().build();
    }
}
