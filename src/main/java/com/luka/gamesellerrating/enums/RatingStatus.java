package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum RatingStatus {

    APPROVED("Approved"),
    PENDING("Pending"),
    REJECTED("Rejected");

    private final String value;

    RatingStatus(String value) {
        this.value = value;
    }
}
