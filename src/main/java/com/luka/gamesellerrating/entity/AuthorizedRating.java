package com.luka.gamesellerrating.entity;

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
@Table(name = "authorized_ratings")
@DiscriminatorValue("AUTHORIZED")
@SQLRestriction("is_deleted = false")
public class AuthorizedRating extends Rating{

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
