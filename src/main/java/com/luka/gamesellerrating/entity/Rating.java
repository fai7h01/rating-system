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
    private RatingValue value;
    private boolean approved;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User author;
    @OneToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @ManyToOne
    private GameObject gameObject;
}
