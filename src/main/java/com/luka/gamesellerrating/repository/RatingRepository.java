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

    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END " +
            "FROM AuthorizedRating ar " +
            "WHERE ar.seller.id = :sellerId AND ar.author.id = :authorId")
    boolean existsAuthorizedRating(@Param("sellerId") Long sellerId, @Param("authorId") Long authorId);

    @Query("SELECT CASE WHEN COUNT(anr) > 0 THEN true ELSE false END " +
            "FROM AnonymousRating anr " +
            "WHERE anr.seller.id = :sellerId " +
            "AND anr.anonymousAuthor.identifier = :identifier")
    boolean existsAnonymousRating(@Param("sellerId") Long sellerId,
                                  @Param("identifier") String sessionId);

    Optional<Rating> findBySellerIdAndId(Long sellerId, Long ratingId);

    @Query(value = """
        SELECT u.*
        FROM ratings r
        JOIN authorized_ratings ar ON r.id = ar.id
        JOIN users u ON ar.authorized_author_id = u.id
        WHERE r.id = :ratingId
        """, nativeQuery = true)
    Optional<User> findAuthorizedAuthor(@Param("ratingId") Long ratingId);

    @Query(value = """
        SELECT au.*
        FROM ratings r
        JOIN anonymous_ratings ar ON r.id = ar.id
        JOIN anonymous_users au ON ar.anonymous_author_id = au.id
        WHERE r.id = :ratingId
        """, nativeQuery = true)
    Optional<AnonymousUser> findAnonymousAuthor(@Param("ratingId") Long ratingId);
}
