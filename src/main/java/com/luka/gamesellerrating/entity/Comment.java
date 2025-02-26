package com.luka.gamesellerrating.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private Long authorId;
    private LocalDateTime createdAt;
    private boolean approved;
    @ManyToOne
    @JoinColumn(name = "game_object_id")
    private GameObject gameObject;

}
