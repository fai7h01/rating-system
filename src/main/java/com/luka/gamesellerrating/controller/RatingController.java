package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/sellers/{sellerId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> submitRating(@PathVariable("sellerId") Long sellerId,
                                                        @RequestBody RatingDTO rating) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Rating submitted successfully.")
                .data(ratingService.save(sellerId, rating))
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper> getSellerRatings(@PathVariable("sellerId") Long sellerId) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Ratings retrieved successfully.")
                .data(ratingService.findAllBySeller(sellerId))
                .build());
    }

    @GetMapping("/{ratingId}")
    public ResponseEntity<ResponseWrapper> getRating(@PathVariable("sellerId") Long sellerId,
                                                     @PathVariable("ratingId") Long ratingId) {
        return ok(ResponseWrapper.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("Rating retrieved successfully.")
                .data(ratingService.findRatingBySeller(sellerId, ratingId))
                .build());
    }
}
