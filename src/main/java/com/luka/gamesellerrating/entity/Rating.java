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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "rating_type", discriminatorType = DiscriminatorType.STRING)
@SQLRestriction("is_deleted = false")
public abstract class Rating extends BaseEntity{

    @Enumerated(EnumType.STRING)
    private RatingValue rating;

    @Column(name = "approved")
    private boolean approved;

    @OneToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    private RatingStatus status;

}
