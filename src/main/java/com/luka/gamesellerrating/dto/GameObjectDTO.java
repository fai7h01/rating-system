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
public class GameObjectDTO {

    @JsonProperty(access = READ_ONLY)
    private Long id;
    private String title;
    private String text;
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime createdAt;
    @JsonProperty(access = READ_ONLY)
    private LocalDateTime updatedAt;
    @JsonProperty(access = READ_ONLY)
    private UserDTO seller;
}
