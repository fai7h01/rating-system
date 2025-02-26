package com.luka.gamesellerrating.dto.wrapper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationExceptionWrapper {

    private String errorField;
    private Object rejectedValue;
    private String reason;

}
