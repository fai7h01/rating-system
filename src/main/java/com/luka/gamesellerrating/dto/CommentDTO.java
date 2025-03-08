package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO {

    @JsonProperty(access = READ_ONLY)
    private Long id;
    private String message;
    @JsonProperty(access = READ_ONLY)
    private String sentimentAnalysis;
}
