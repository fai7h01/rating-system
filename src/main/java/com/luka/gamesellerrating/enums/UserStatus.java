package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    ACTIVE("Active"),
    PENDING("Pending"),
    REJECTED("Rejected");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}
