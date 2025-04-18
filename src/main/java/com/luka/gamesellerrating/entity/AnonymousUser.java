package com.luka.gamesellerrating.entity;

import com.github.javafaker.Faker;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "anonymous_users")
@SQLRestriction("is_deleted = false")
@ToString
public class AnonymousUser extends BaseEntity{

    private String username;
    private String identifier;

    public AnonymousUser() {
        this.username = Faker.instance().name().username();
    }

    public static AnonymousUser create() {
        return new AnonymousUser();
    }
}
