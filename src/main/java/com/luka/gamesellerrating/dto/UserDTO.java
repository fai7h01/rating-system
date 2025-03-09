package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
import lombok.*;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @JsonProperty(access = READ_ONLY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    @JsonProperty(access = READ_ONLY)
    private String confirmPassword;
    private Role role;
    @JsonProperty(access = READ_ONLY)
    private BigDecimal overallRating;
    @JsonProperty(access = READ_ONLY)
    private UserStatus status = UserStatus.PENDING;
}
