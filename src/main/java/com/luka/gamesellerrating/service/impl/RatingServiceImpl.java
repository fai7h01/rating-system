package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.*;
import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.enums.RatingValue;
import com.luka.gamesellerrating.exception.RatingNotFoundException;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.*;
import com.luka.gamesellerrating.service.helper.RatingFactory;
import com.luka.gamesellerrating.service.helper.RatingValidator;
import com.luka.gamesellerrating.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

@Slf4j
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingFactory ratingFactory;
    private final RatingValidator ratingValidator;
    private final MapperUtil mapperUtil;

    public RatingServiceImpl(RatingRepository ratingRepository, MapperUtil mapperUtil, RatingFactory ratingFactory, RatingValidator ratingValidator) {
        this.ratingRepository = ratingRepository;
        this.mapperUtil = mapperUtil;
        this.ratingFactory = ratingFactory;
        this.ratingValidator = ratingValidator;
    }

    @Override
    @Transactional
    public RatingDTO save(Long sellerId, RatingDTO rating) {
        var preparedRating = ratingFactory.createRating(sellerId, rating);
        ratingValidator.validateDuplicateRating(preparedRating);
        var savedRating = ratingRepository.save(preparedRating);
        return mapperUtil.convert(savedRating, new RatingDTO());
    }

    @Override
    @Transactional
    public RatingDTO update(Long sellerId, Long ratingId, RatingDTO ratingDTO) {
        var ratingEntity = findRatingBySellerAndId(sellerId, ratingId);
        ratingValidator.validateUserAccess(ratingEntity);
        updateRatingFields(ratingEntity, ratingDTO);
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
        var foundRating = findRatingBySellerAndId(sellerId, ratingId);
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
        ratingValidator.validateUserAccess(rating);
        rating.setIsDeleted(true);
        ratingRepository.save(rating);
    }


    private Rating findRatingBySellerAndId(Long sellerId, Long ratingId) {
        return ratingRepository.findBySellerIdAndId(sellerId, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found"));
    }

    private void updateRatingFields(Rating entity, RatingDTO dto) {
        entity.setRating(dto.getRating());
        entity.getComment().setMessage(dto.getComment().getMessage());
    }

}
