package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByAuthorId(Long id);
    List<Rating> findAllByGameObjectSellerId(Long id);

}
