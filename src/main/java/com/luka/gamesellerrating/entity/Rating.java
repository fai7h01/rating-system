package com.luka.gamesellerrating.entity;

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
    private RatingValue rating;

    @Column(name = "approved")
    private boolean approved;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "anonymous_author_id")
    private AnonymousUser anonymousUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "game_object_id", nullable = false)
    private GameObject gameObject;

}
