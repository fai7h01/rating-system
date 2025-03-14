package com.luka.gamesellerrating.entity;

import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.enums.RatingValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ratings")
@SQLRestriction("is_deleted = false")
public class Rating extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private RatingValue value;

    @OneToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "authorized_author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "anonymous_author_id")
    private AnonymousUser anonymousAuthor;

    @Enumerated(EnumType.STRING)
    private RatingStatus status;

    private boolean isAnonymous;
}
