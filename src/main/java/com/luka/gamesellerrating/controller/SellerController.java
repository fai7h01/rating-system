package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/sellers")
public class SellerController {

    private final UserService userService;

    public SellerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> findAllSellers(@RequestParam(value = "sort", defaultValue = "overallRating") String sortBy,
                                                          @RequestParam(value = "order", defaultValue = "DESC") String order) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Sellers retrieved successfully")
                .data(userService.findAllSellers(Sort.by(Sort.Direction.fromString(order), sortBy)))
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper> findSellersByUsername(@RequestParam("username") String username) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Seller(s) retrieved successfully")
                .data(userService.findSellersByUsernameContaining(username))
                .build());
    }

    @GetMapping("/top")
    public ResponseEntity<ResponseWrapper> findTopSellers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "limit", defaultValue = "3") int limit) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Top sellers retrieved successfully")
                .data(userService.findTopSellers(page, limit))
                .build());
    }
}
