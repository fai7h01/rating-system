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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_objects")
@SQLRestriction("is_deleted = false")
public class GameObject extends BaseEntity{

    private String title;
    private String text;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
}
