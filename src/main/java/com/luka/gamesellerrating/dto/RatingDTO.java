package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    private Long id;
    private RatingValue rating;
    private boolean approved;
    private UserDTO author;
    private AnonymousUserDTO anonymousAuthor;
    private CommentDTO comment;
    private GameObjectDTO gameObject;
}
