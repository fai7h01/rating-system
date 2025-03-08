package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.AnonymousRating;
import com.luka.gamesellerrating.entity.AuthorizedRating;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.RatingService;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final AnonymousUserService anonymousUserService;
    private final UserService userService;
    private final KeycloakService keycloakService;
    private final MapperUtil mapperUtil;

    public RatingServiceImpl(RatingRepository ratingRepository, AnonymousUserService anonymousUserService, UserService userService,
                             KeycloakService keycloakService, MapperUtil mapperUtil) {
        this.ratingRepository = ratingRepository;
        this.anonymousUserService = anonymousUserService;
        this.userService = userService;
        this.keycloakService = keycloakService;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public RatingDTO save(Long sellerId, RatingDTO rating, String sessionId, String ipAddress) {
        if (keycloakService.isUserAnonymous()) {
            return saveAnonymous(sellerId, rating, sessionId, ipAddress);
        }
        return saveAuthorized(sellerId, rating);
    }

    private RatingDTO saveAnonymous(Long sellerId, RatingDTO rating, String sessionId, String ipAddress) {
        //check if seller exists
        var seller = userService.findById(sellerId);
        if (ratingRepository.existsAnonymousRating(sellerId, sessionId, ipAddress)) {
            throw new RuntimeException("You have already rated this seller.");
        }
        var anonymousUser = anonymousUserService.findBySessionIdAndIpAddress(sessionId, ipAddress)
                .orElseGet(() -> anonymousUserService.save(sessionId, ipAddress));
        rating.setSeller(seller);
        rating.setAnonymousAuthor(anonymousUser);
        var ratingToSave = mapperUtil.convert(rating, new AnonymousRating());
        var savedRating = ratingRepository.save(ratingToSave);
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    private RatingDTO saveAuthorized(Long sellerId, RatingDTO rating) {
        var loggedInUser = keycloakService.getLoggedInUser();
        var seller = userService.findById(sellerId);
        if (ratingRepository.existsAuthorizedRating(sellerId, loggedInUser.getId())) {
            throw new RuntimeException("You have already rated this seller.");
        }
        rating.setSeller(seller);
        rating.setAuthor(loggedInUser);
        var ratingToSave = mapperUtil.convert(rating, new AuthorizedRating());
        var savedRating = ratingRepository.save(ratingToSave);
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    @Override
    public List<RatingDTO> findAllBySeller(Long sellerId) {
        return ratingRepository.findAllBySellerId(sellerId)
                .stream()
                .map(rating -> mapperUtil.convert(rating, new RatingDTO()))
                .toList();
    }

}
