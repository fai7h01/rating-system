package com.luka.gamesellerrating.controller;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.dto.wrapper.ResponseWrapper;
import com.luka.gamesellerrating.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellers/{sellerId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> submitRating(@PathVariable("sellerId") Long sellerId,
                                                        @RequestBody RatingDTO rating,
                                                        HttpServletRequest request) {
        RatingDTO savedRating = ratingService.save(sellerId, rating, request.getSession().getId(), request.getRemoteAddr());
        return ResponseEntity.ok(ResponseWrapper.builder()
                .data(savedRating)
                .message("Rating submitted successfully.").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> findAllBySeller(@PathVariable Long id) {
        return ResponseEntity.ok(ResponseWrapper.builder().data(ratingService.findAllBySeller(id)).build());
    }
}
