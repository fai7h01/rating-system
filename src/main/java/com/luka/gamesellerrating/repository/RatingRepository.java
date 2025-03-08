package com.luka.gamesellerrating.repository;

import com.luka.gamesellerrating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllBySellerId(Long sellerId);

    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END " +
            "FROM AuthorizedRating ar " +
            "WHERE ar.seller.id = :sellerId AND ar.author.id = :authorId")
    boolean existsAuthorizedRating(@Param("sellerId") Long sellerId, @Param("authorId") Long authorId);

    @Query("SELECT CASE WHEN COUNT(anr) > 0 THEN true ELSE false END " +
            "FROM AnonymousRating anr " +
            "WHERE anr.seller.id = :sellerId " +
            "AND anr.anonymousAuthor.sessionId = :sessionId " +
            "AND anr.anonymousAuthor.ipAddress = :ipAddress")
    boolean existsAnonymousRating(@Param("sellerId") Long sellerId,
                                  @Param("sessionId") String sessionId,
                                  @Param("ipAddress") String ipAddress);
}
