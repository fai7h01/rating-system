package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private Role role;
    private UserStatus status;

}
