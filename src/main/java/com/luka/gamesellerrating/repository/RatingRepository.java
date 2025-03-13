package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.AnonymousUser;
import com.luka.gamesellerrating.entity.Rating;
import com.luka.gamesellerrating.entity.User;
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

    @Query("SELECT r.author FROM Rating r WHERE r.isAnonymous = false AND r.id = :ratingId")
    Optional<User> findAuthorizedAuthor(@Param("ratingId") Long ratingId);

    @Query("SELECT r.anonymousAuthor FROM Rating r WHERE r.isAnonymous = true AND r.id = :ratingId")
    Optional<AnonymousUser> findAnonymousAuthor(@Param("ratingId") Long ratingId);
}
