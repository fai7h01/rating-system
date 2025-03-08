package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.AnonymousRating;
import com.luka.gamesellerrating.entity.AuthorizedRating;
import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.exception.RatingAlreadyExistsException;
import com.luka.gamesellerrating.exception.RatingNotFoundException;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.service.KeycloakService;
import com.luka.gamesellerrating.service.RatingService;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.util.MapperUtil;
import jakarta.transaction.Transactional;
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
        var seller = userService.findById(sellerId);
        rating.setSeller(seller);
        return keycloakService.isUserAnonymous()
                ? saveAnonymous(rating, sessionId, ipAddress)
                : saveAuthorized(rating);
    }


    @Transactional
    @Override
    public List<RatingDTO> findAllBySeller(Long sellerId) {
        return ratingRepository.findAllBySellerId(sellerId)
                .stream()
                .map(rating -> mapperUtil.convert(rating, new RatingDTO()))
                .toList();
    }

    @Override
    public RatingDTO findRatingBySeller(Long sellerId, Long ratingId) {
        var foundRating = ratingRepository.findBySellerIdAndId(sellerId, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found."));
        return mapperUtil.convert(foundRating, new RatingDTO());
    }

    private RatingDTO saveAnonymous(RatingDTO rating, String sessionId, String ipAddress) {
        checkDuplicateAnonymousRating(rating.getSeller().getId(), sessionId, ipAddress);
        var anonymousUser = getOrCreateAnonymousUser(sessionId, ipAddress);
        rating.setAnonymousAuthor(anonymousUser);
        return persistRating(rating, new AnonymousRating());
    }


    private RatingDTO saveAuthorized(RatingDTO rating) {
        var loggedInUser = keycloakService.getLoggedInUser();
        checkDuplicateAuthorizedRating(rating.getSeller().getId(), loggedInUser.getId());
        rating.setAuthor(loggedInUser);
        return persistRating(rating, new AuthorizedRating());
    }

    private <T extends Rating> RatingDTO persistRating(RatingDTO rating, T ratingClass) {
        var ratingToSave = mapperUtil.convert(rating, ratingClass);
        var savedRating = ratingRepository.save(ratingToSave);
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    private void checkDuplicateAnonymousRating(Long sellerId, String sessionId, String ipAddress) {
        if (ratingRepository.existsAnonymousRating(sellerId, sessionId, ipAddress)) {
            throw new RatingAlreadyExistsException("You have already rated this seller.");
        }
    }

    private void checkDuplicateAuthorizedRating(Long sellerId, Long userId) {
        if (ratingRepository.existsAuthorizedRating(sellerId, userId)) {
            throw new RatingAlreadyExistsException("You have already rated this seller.");
        }
    }

    private AnonymousUserDTO getOrCreateAnonymousUser(String sessionId, String ipAddress) {
        return anonymousUserService.findBySessionIdAndIpAddress(sessionId, ipAddress)
                .orElseGet(() -> anonymousUserService.save(sessionId, ipAddress));
    }

}
