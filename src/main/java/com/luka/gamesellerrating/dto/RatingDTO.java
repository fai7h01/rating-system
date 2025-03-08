package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.luka.gamesellerrating.enums.RatingValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private RatingValue rating;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean approved;
    private CommentDTO comment;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDTO author;
    private UserDTO seller;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private AnonymousUserDTO anonymousAuthor;
}
