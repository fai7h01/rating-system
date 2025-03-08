package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.RatingDTO;

import java.util.List;

public interface RatingService {

    RatingDTO save(Long sellerId, RatingDTO rating, String sessionId, String ipAddress);
    List<RatingDTO> findAllBySeller(Long sellerId);
}
