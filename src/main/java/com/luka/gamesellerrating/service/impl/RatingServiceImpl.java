package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.AnonymousRating;
import com.luka.gamesellerrating.entity.AuthorizedRating;
import com.luka.gamesellerrating.entity.Comment;
import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.exception.RatingAlreadyExistsException;
import com.luka.gamesellerrating.exception.RatingNotFoundException;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.*;
import com.luka.gamesellerrating.util.MapperUtil;
import com.luka.gamesellerrating.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public RatingDTO save(Long sellerId, RatingDTO rating) {
        var seller = userService.findById(sellerId);
        rating.setSeller(seller);
        return keycloakService.isUserAnonymous()
                ? saveAnonymous(rating, requestUtil)
                : saveAuthorized(rating);
    }

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

    private RatingDTO saveAnonymous(RatingDTO rating, RequestUtil requestUtil) {
        var identifier = requestUtil.getSessionId().concat("-").concat(requestUtil.getClientIp());
        checkDuplicateAnonymousRating(rating.getSeller().getId(),identifier);
        var anonymousUser = getOrCreateAnonymousUser(identifier);
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
        var savedComment = commentService.save(rating.getComment());
        ratingToSave.setComment(mapperUtil.convert(savedComment, new Comment()));
        var savedRating = ratingRepository.save(ratingToSave);
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    private void checkDuplicateAnonymousRating(Long sellerId, String identifier) {
        if (ratingRepository.existsAnonymousRating(sellerId, identifier)) {
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
