package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.RatingDTO;
import com.luka.gamesellerrating.entity.*;
import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.exception.RatingNotFoundException;
import com.luka.gamesellerrating.mapper.RatingMapper;
import com.luka.gamesellerrating.repository.RatingRepository;
import com.luka.gamesellerrating.service.*;
import com.luka.gamesellerrating.service.helper.RatingFactory;
import com.luka.gamesellerrating.service.helper.RatingValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingFactory ratingFactory;
    private final RatingValidator ratingValidator;
    private final RatingMapper ratingMapper;

    public RatingServiceImpl(RatingRepository ratingRepository, RatingFactory ratingFactory, RatingValidator ratingValidator, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingFactory = ratingFactory;
        this.ratingValidator = ratingValidator;
        this.ratingMapper = ratingMapper;
    }

    @Override
    @Transactional
    public RatingDTO save(Long sellerId, RatingDTO rating) {
        var preparedRating = ratingFactory.createRating(sellerId, rating);
        ratingValidator.validateDuplicateRating(preparedRating);
        var savedRating = ratingRepository.save(preparedRating);
        return ratingMapper.toDto(savedRating);
    }

    @Override
    @Transactional
    public RatingDTO update(Long sellerId, Long ratingId, RatingDTO ratingDTO) {
        var ratingEntity = findRatingEntityBySellerAndId(sellerId, ratingId);
        ratingValidator.validateUserAccess(ratingEntity);
        updateRatingFields(ratingEntity, ratingDTO);
        return ratingMapper.toDto(ratingRepository.save(ratingEntity));
    }


    @Override
    public List<RatingDTO> findAllBySeller(Long sellerId) {
        return ratingMapper.toDtoList(ratingRepository.findAllBySellerId(sellerId));
    }

    @Override
    public RatingDTO findBySeller(Long sellerId, Long ratingId) {
        var foundRating = findRatingEntityBySellerAndId(sellerId, ratingId);
        return ratingMapper.toDto(foundRating);
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
        var rating = findRatingEntityBySeller(sellerId, ratingId);
        ratingValidator.validateUserAccess(rating);
        rating.setIsDeleted(true);
        ratingRepository.save(rating);
    }


    private Rating findRatingEntityBySellerAndId(Long sellerId, Long ratingId) {
        return findRatingEntityBySeller(sellerId, ratingId);
    }

    private Rating findRatingEntityBySeller(Long sellerId, Long ratingId) {
        return ratingRepository.findBySellerIdAndId(sellerId, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Rating not found"));
    }

    private void updateRatingFields(Rating entity, RatingDTO dto) {
        entity.setValue(dto.getStars());
        entity.getComment().setMessage(dto.getComment().getMessage());
    }

}
