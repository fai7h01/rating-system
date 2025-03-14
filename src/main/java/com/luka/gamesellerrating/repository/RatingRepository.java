package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.enums.RatingValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllBySellerId(Long sellerId);

    @Query("SELECT COUNT(r) > 0 FROM Rating r WHERE r.isAnonymous = false AND r.seller.id = :sellerId AND r.author.id = :authorId")
    boolean existsAuthorizedBySellerIdAndAuthorId(@Param("sellerId") Long sellerId, @Param("authorId") Long authorId);

    @Query("SELECT COUNT(r) > 0 FROM Rating r WHERE r.isAnonymous = true AND r.seller.id = :sellerId AND r.anonymousAuthor.identifier = :authorId")
    boolean existsAnonymousBySellerIdAndAuthorId(@Param("sellerId") Long sellerId, @Param("authorId") String authorId);

    Optional<Rating> findBySellerIdAndId(Long sellerId, Long ratingId);

    @Query("SELECT r.value FROM Rating r WHERE r.seller.id = :sellerId AND r.status = 'APPROVED'")
    List<RatingValue> findAllRatingValueBySeller(@Param("sellerId") Long sellerId);
}
