package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.*;
import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.exception.RatingAccessDeniedException;
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
    public RatingDTO update(Long sellerId, Long ratingId, RatingDTO ratingDTO) {
        var ratingEntity = ratingRepository.findBySellerIdAndId(sellerId, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found."));
        validateUserAccess(ratingEntity);
        ratingEntity.setRating(ratingDTO.getRating());
        ratingDTO.getComment().setId(ratingEntity.getComment().getId());
        var updatedComment = commentService.save(ratingDTO.getComment());
        ratingEntity.setComment(mapperUtil.convert(updatedComment, new Comment()));
        return mapperUtil.convert(ratingRepository.save(ratingEntity), new RatingDTO());
    }

    @Override
    public List<RatingDTO> findAllBySeller(Long sellerId) {
        return ratingRepository.findAllBySellerId(sellerId).stream()
                .map(mapperUtil.convertTo(RatingDTO.class))
                .toList();
    }

    @Override
    public RatingDTO findBySeller(Long sellerId, Long ratingId) {
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

    @Override
    public void delete(Long sellerId, Long ratingId) {
        var rating = ratingRepository.findBySellerIdAndId(sellerId, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found."));

        validateUserAccess(rating);
        rating.setIsDeleted(true);
        ratingRepository.save(rating);
    }


    private void validateUserAccess(Rating rating) {
       validateUserTypeMatch(rating);
       validateAuthorEligibility(rating);
    }

    private void validateUserTypeMatch(Rating rating) {
        boolean isCurrentUserAnonymous = keycloakService.isUserAnonymous();
        if (isCurrentUserAnonymous != rating.isAnonymous()) {
            throw new RatingAccessDeniedException("Only the author can manage their own rating");
        }
    }

    private void validateAuthorEligibility(Rating rating) {
        boolean isCurrentUserAnonymous = keycloakService.isUserAnonymous();
        boolean isEligible = isCurrentUserAnonymous
                ? isAnonymousUserEligible(rating.getId())
                : isAuthorizedUserEligible(rating.getId());

        if (!isEligible) {
            throw new RatingAccessDeniedException("Only the author can manage their own rating");
        }
    }

    private boolean isAnonymousUserEligible(Long ratingId) {
        String currentFingerprint = requestUtil.generateDeviceFingerprint();
        var anonymousAuthor = ratingRepository.findAnonymousAuthor(ratingId);
        return anonymousAuthor != null &&
                anonymousAuthor.getIdentifier().equals(currentFingerprint);
    }

    private boolean isAuthorizedUserEligible(Long ratingId) {
        var currentUser = keycloakService.getLoggedInUser();
        var authorizedAuthor = ratingRepository.findAuthorizedAuthor(ratingId);
        return authorizedAuthor != null &&
                authorizedAuthor.getId().equals(currentUser.getId());
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
        rating.setAnonymous(true);
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
