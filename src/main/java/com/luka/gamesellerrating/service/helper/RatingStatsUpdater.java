package com.luka.gamesellerrating.service.helper;

import com.luka.gamesellerrating.entity.User;
import com.luka.gamesellerrating.enums.RatingValue;
import com.luka.gamesellerrating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

@Component
@RequiredArgsConstructor
public class RatingStatsUpdater {

    private final RatingRepository ratingRepository;

    public void updateUserRating(User user) {
        var overallRating = calculateOverallRating(user.getId());
        user.setOverallRating(overallRating);
    }

    public BigDecimal calculateOverallRating(Long sellerId) {
        var ratingValues = ratingRepository.findAllRatingValueBySeller(sellerId);
        if (ratingValues.isEmpty()) {
            return ZERO;
        }
        return calculateAverageRating(ratingValues);
    }

    private BigDecimal calculateAverageRating(List<RatingValue> ratingValues) {
        var ratingValueSum = ratingValues.stream()
                .mapToInt(RatingValue::getValue)
                .sum();
        return valueOf(ratingValueSum)
                .divide(valueOf(ratingValues.size()), 2, HALF_UP);
    }
}
