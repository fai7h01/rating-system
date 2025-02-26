package com.luka.gamesellerrating.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper {

    private boolean success;
    private String message;
    private String alert;
    private Integer code;
    private Object data;

}
