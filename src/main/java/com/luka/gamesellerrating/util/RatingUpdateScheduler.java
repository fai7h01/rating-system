package com.luka.gamesellerrating.util;

import com.luka.gamesellerrating.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RatingUpdateScheduler {

    private final UserService userService;

    @Transactional
    @Scheduled(fixedRate = 5_000)
    public void updateAllSellersOverallRating() {
        var allSellers = userService.findAllSellers(Sort.unsorted());
        allSellers.forEach(seller -> userService.updateOverallRating(seller.getId()));
    }
}
