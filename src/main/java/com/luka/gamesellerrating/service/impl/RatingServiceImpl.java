package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.AnonymousRating;
import com.luka.gamesellerrating.entity.AuthorizedRating;
import com.luka.gamesellerrating.entity.Comment;
import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.exception.RatingAlreadyExistsException;
import com.luka.gamesellerrating.exception.RatingNotFoundException;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.*;
import com.luka.gamesellerrating.util.MapperUtil;
import com.luka.gamesellerrating.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final AnonymousUserService anonymousUserService;
    private final UserService userService;
    private final KeycloakService keycloakService;
    private final CommentService commentService;
    private final RequestUtil requestUtil;
    private final MapperUtil mapperUtil;

    public RatingServiceImpl(RatingRepository ratingRepository, AnonymousUserService anonymousUserService, UserService userService,
                             KeycloakService keycloakService, CommentService commentService, RequestUtil requestUtil, MapperUtil mapperUtil) {
        this.ratingRepository = ratingRepository;
        this.anonymousUserService = anonymousUserService;
        this.userService = userService;
        this.keycloakService = keycloakService;
        this.commentService = commentService;
        this.requestUtil = requestUtil;
        this.mapperUtil = mapperUtil;
    }


    @Override
    @Transactional
    public RatingDTO save(Long sellerId, RatingDTO rating) {
        var seller = userService.findById(sellerId);
        rating.setSeller(seller);
        boolean isUserAnonymous = keycloakService.isUserAnonymous();
        var preparedRating = isUserAnonymous
                ? prepareAnonymousRating(rating)
                : prepareAuthorizedRating(rating);
        var targetEntity = isUserAnonymous ? new AnonymousRating() : new AuthorizedRating();
        return persistRating(preparedRating, targetEntity);
    }

    @Override
    public List<RatingDTO> findAllBySeller(Long sellerId) {
        return ratingRepository.findAllBySellerId(sellerId).stream()
                .map(mapperUtil.convertTo(RatingDTO.class))
                .toList();
    }

    @Override
    public RatingDTO findRatingBySeller(Long sellerId, Long ratingId) {
        var foundRating = ratingRepository.findBySellerIdAndId(sellerId, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found."));
        return mapperUtil.convert(foundRating, new RatingDTO());
    }

    @Override
    public void updateStatus(Long ratingId, RatingStatus status) {
        var rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found."));
        rating.setStatus(status);
        ratingRepository.save(rating);
    }

    private <T extends Rating> RatingDTO persistRating(RatingDTO rating, T targetEntity) {
        var ratingToSave = mapperUtil.convert(rating, targetEntity);
        var savedComment = commentService.save(rating.getComment());
        ratingToSave.setComment(mapperUtil.convert(savedComment, new Comment()));
        var savedRating = ratingRepository.save(ratingToSave);
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    private RatingDTO prepareAnonymousRating(RatingDTO rating) {
        var identifier = requestUtil.generateDeviceFingerprint();
        checkDuplicateAnonymousRating(rating.getSeller().getId(), identifier);
        var anonymousUser = getOrCreateAnonymousUser(identifier);
        rating.setAnonymousAuthor(anonymousUser);
        return rating;
    }


    private RatingDTO prepareAuthorizedRating(RatingDTO rating) {
        var loggedInUser = keycloakService.getLoggedInUser();
        checkDuplicateAuthorizedRating(rating.getSeller().getId(), loggedInUser.getId());
        rating.setAuthor(loggedInUser);
        return rating;
    }

    private void checkDuplicateAnonymousRating(Long sellerId, String anonymousIdentifier) {
        if (ratingRepository.existsAnonymousRating(sellerId, anonymousIdentifier)) {
            throw new RatingAlreadyExistsException("You have already rated this seller.");
        }
    }

    private void checkDuplicateAuthorizedRating(Long sellerId, Long userId) {
        if (ratingRepository.existsAuthorizedRating(sellerId, userId)) {
            throw new RatingAlreadyExistsException("You have already rated this seller.");
        }
    }

    private AnonymousUserDTO getOrCreateAnonymousUser(String identifier) {
        return anonymousUserService.findByIdentifier(identifier)
                .orElseGet(() -> anonymousUserService.save(identifier));
    }

}
