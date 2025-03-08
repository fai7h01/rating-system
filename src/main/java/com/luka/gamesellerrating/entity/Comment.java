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
@Table(name = "comments")
@SQLRestriction("is_deleted = false")
public class Comment extends BaseEntity{

    @Column(nullable = false)
    private String message;
    private String sentimentAnalysis;
}
