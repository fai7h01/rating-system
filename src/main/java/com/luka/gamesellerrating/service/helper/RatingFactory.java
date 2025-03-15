package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.dto.AnonymousUserDTO;
import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.mapper.RatingMapper;
import com.luka.gamesellerrating.service.AnonymousUserService;
import com.luka.gamesellerrating.service.AuthenticationService;
import com.luka.gamesellerrating.service.CommentService;
import com.luka.gamesellerrating.service.UserService;
import com.luka.gamesellerrating.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingFactory {

    private final AuthenticationService authService;
    private final UserService userService;
    private final AnonymousUserService anonymousUserService;
    private final CommentService commentService;
    private final RatingMapper ratingMapper;
    private final RequestUtil requestUtil;

    public Rating createRating(Long sellerId, RatingDTO rating) {
        var seller = userService.findById(sellerId);
        rating.setSeller(seller);
        var readyRating = prepareComment(rating);
        var createdRating = authService.isUserAnonymous()
                ? configureAnonymousRating(readyRating)
                : configureAuthorizedRating(readyRating);
        return ratingMapper.toEntity(createdRating);
    }

    private RatingDTO prepareComment(RatingDTO ratingDto) {
        var savedCommentDto = commentService.save(ratingDto.getComment());
        ratingDto.setComment(savedCommentDto);
        var ratingEntity = ratingMapper.toEntity(ratingDto);
        return ratingMapper.toDto(ratingEntity);
    }

    private RatingDTO configureAnonymousRating(RatingDTO rating) {
        var currentFingerprint = requestUtil.generateDeviceFingerprint();
        var anonymousUser = getOrCreateAnonymousUser(currentFingerprint);
        rating.setAnonymousAuthor(anonymousUser);
        rating.setAnonymous(true);
        return rating;
    }

    private RatingDTO configureAuthorizedRating(RatingDTO rating) {
        rating.setAuthor(authService.getLoggedInUser());
        rating.setAnonymous(false);
        return rating;
    }

    private AnonymousUserDTO getOrCreateAnonymousUser(String identifier) {
        return anonymousUserService.findByIdentifier(identifier)
                .orElseGet(() -> anonymousUserService.save(identifier));
    }
}
