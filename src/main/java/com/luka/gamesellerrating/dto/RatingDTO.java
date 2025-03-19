package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luka.gamesellerrating.enums.RatingStatus;
import com.luka.gamesellerrating.enums.RatingValue;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class RatingDTO {

    @JsonProperty(access = READ_ONLY)
    private Long id;
    @NotNull
    private RatingValue stars;
    @NotNull
    private CommentDTO comment;
    @JsonProperty(access = READ_ONLY)
    private UserDTO author;
    @JsonProperty(access = READ_ONLY)
    private UserDTO seller;
    @JsonProperty(access = READ_ONLY)
    private AnonymousUserDTO anonymousAuthor;
    @JsonProperty(access = READ_ONLY)
    private RatingStatus status = RatingStatus.PENDING;
    @JsonProperty(access = READ_ONLY)
    private boolean anonymous;
}
