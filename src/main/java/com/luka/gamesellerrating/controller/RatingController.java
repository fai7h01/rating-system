package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/sellers/{id}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> submitRating(@PathVariable("id") Long sellerId,
                                                        @RequestBody RatingDTO rating) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Rating submitted successfully.")
                .data(ratingService.save(sellerId, rating))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> getSellerRatings(@PathVariable("id") Long sellerId) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Ratings retrieved successfully.")
                .data(ratingService.findAllBySeller(sellerId))
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getRating(@PathVariable("id") Long sellerId,
                                                     @PathVariable("id") Long ratingId) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Rating retrieved successfully.")
                .data(ratingService.findRatingBySeller(sellerId, ratingId))
                .build());
    }
}
