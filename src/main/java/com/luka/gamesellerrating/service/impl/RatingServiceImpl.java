package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.RatingService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final AnonymousUserService anonymousUserService;
    private final KeycloakService keycloakService;
    private final MapperUtil mapperUtil;

    public RatingServiceImpl(RatingRepository ratingRepository, AnonymousUserService anonymousUserService,
                             KeycloakService keycloakService, MapperUtil mapperUtil) {
        this.ratingRepository = ratingRepository;
        this.anonymousUserService = anonymousUserService;
        this.keycloakService = keycloakService;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public RatingDTO save(RatingDTO rating, String sessionId, String ipAddress) {
        if (keycloakService.isUserAnonymous()) {
            var savedAnonymousUser = anonymousUserService.save(sessionId, ipAddress);
            rating.setAnonymousAuthor(savedAnonymousUser);
        } else {
            rating.setAuthor(keycloakService.getLoggedInUser());
        }
        var savedRating = ratingRepository.save(mapperUtil.convert(rating, new Rating()));
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    @Override
    public List<RatingDTO> findAllBySeller(Long sellerId) {
        return ratingRepository.findAllByGameObjectSellerId(sellerId)
                .stream()
                .map(rating -> mapperUtil.convert(rating, new RatingDTO()))
                .toList();
    }

    @Override
    public void delete(Long id) {

    }


}
