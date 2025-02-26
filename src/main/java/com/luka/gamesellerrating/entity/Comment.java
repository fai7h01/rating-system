package com.luka.gamesellerrating.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@SQLRestriction("is_deleted = false")
public class Comment extends BaseEntity{

    private String message;
    private LocalDateTime createdAt;
    private boolean approved;
    @ManyToOne
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;
    @ManyToOne
    @JoinColumn(name = "game_object_id", nullable = false)
    private GameObject gameObject;

}
