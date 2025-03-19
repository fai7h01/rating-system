package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.exception.RatingAccessDeniedException;
import com.luka.gamesellerrating.exception.RatingAlreadyExistsException;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.AuthenticationService;
import com.luka.gamesellerrating.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingValidator {

    private final RatingRepository ratingRepository;
    private final AuthenticationService authService;
    private final RequestUtil requestUtil;

    public void validateDuplicateRating(Rating rating) {
        var sellerId = rating.getSeller().getId();
        var alreadyExists = rating.isAnonymous()
                ? ratingRepository.existsAnonymousBySellerIdAndAuthorId(sellerId, rating.getAnonymousAuthor().getIdentifier())
                : ratingRepository.existsAuthorizedBySellerIdAndAuthorId(sellerId, rating.getAuthor().getId());
        if (alreadyExists) {
            throw new RatingAlreadyExistsException("You have already rated this seller");
        }
    }

    public void validateUserAccess(Rating rating) {
        var isCurrentUserAnonymous = authService.isUserAnonymous();
        if (isCurrentUserAnonymous != rating.isAnonymous()) {
            throw new RatingAccessDeniedException("Only the author can manage their own rating");
        }
        boolean isEligible = isCurrentUserAnonymous
                ? isAnonymousUserEligible(rating)
                : isAuthorizedUserEligible(rating);
        if (!isEligible) {
            throw new RatingAccessDeniedException("Only the author can manage their own rating");
        }
    }

    private boolean isAnonymousUserEligible(Rating rating) {
        var currentFingerprint = requestUtil.generateDeviceFingerprint();
        return rating.getAnonymousAuthor() != null &&
                rating.getAnonymousAuthor().getIdentifier().equals(currentFingerprint);
    }

    private boolean isAuthorizedUserEligible(Rating rating) {
        var currentUser = authService.getLoggedInUser();
        return rating.getAuthor() != null && rating.getAuthor().getId().equals(currentUser.getId());
    }
}
