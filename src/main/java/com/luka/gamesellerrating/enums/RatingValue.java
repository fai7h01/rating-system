package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum RatingValue {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    RatingValue(int value) {
        this.value = value;
    }

}
