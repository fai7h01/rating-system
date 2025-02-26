package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
