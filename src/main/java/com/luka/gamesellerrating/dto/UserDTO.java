package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luka.gamesellerrating.annotation.PasswordMatch;
import com.luka.gamesellerrating.enums.Role;
import com.luka.gamesellerrating.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@PasswordMatch(message = "{PasswordMatch.user}")
public class UserDTO {

    @JsonProperty(access = READ_ONLY)
    private Long id;
    @NotBlank(message = "{NotBlank.user.firstName}")
    @Size(max = 15, min = 2, message = "{Size.user.firstName}")
    private String firstName;
    @NotBlank(message = "{NotBlank.user.lastName}")
    @Size(max = 15, min = 2, message = "{Size.user.lastName}")
    private String lastName;
    @NotBlank(message = "{NotBlank.user.email}")
    @Email(message = "{Email.user.email}")
    private String email;
    @JsonProperty(access = READ_ONLY)
    private boolean emailVerified;
    @NotBlank(message = "{NotBlank.user.username}")
    private String username;
    @JsonProperty(access = WRITE_ONLY)
    @NotBlank(message = "{NotBlank.user.password}")
    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}",
            message = "{Pattern.user.password}")
    private String password;
    @NotNull
    @JsonProperty(access = WRITE_ONLY)
    private String confirmPassword;
    @NotNull(message = "{NotNull.user.role}")
    private Role role;
    @JsonProperty(access = READ_ONLY)
    private BigDecimal overallRating;
    @JsonProperty(access = READ_ONLY)
    private UserStatus status = UserStatus.PENDING;
}
