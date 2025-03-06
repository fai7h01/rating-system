package com.luka.gamesellerrating.entity;

import jakarta.persistence.Entity;
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
@Table(name = "anonymous_users")
@SQLRestriction("is_deleted = false")
public class AnonymousUser extends BaseEntity{

    private String username;
    private String sessionId;
    private String ipAddress;

}
