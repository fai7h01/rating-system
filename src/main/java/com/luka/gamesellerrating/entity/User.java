package com.luka.gamesellerrating.entity;

import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
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
@Table(name = "users")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity{

    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;
    private String confirmPassword;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING;

}
