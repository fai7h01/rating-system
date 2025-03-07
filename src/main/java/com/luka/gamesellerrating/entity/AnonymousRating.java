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
@Table(name = "anonymous_ratings")
@DiscriminatorValue("ANONYMOUS")
@SQLRestriction("is_deleted = false")
public class AnonymousRating extends Rating{

    @ManyToOne
    @JoinColumn(name = "anonymous_author_id", nullable = false)
    private AnonymousUser anonymousAuthor;
}
