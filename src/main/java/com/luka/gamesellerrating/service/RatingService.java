package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.enums.RatingStatus;

import java.util.List;

public interface RatingService {

    RatingDTO save(Long sellerId, RatingDTO rating);
    List<RatingDTO> findAllBySeller(Long sellerId);
    RatingDTO findRatingBySeller(Long sellerId, Long ratingId);
    void updateStatus(Long ratingId, RatingStatus status);
}
