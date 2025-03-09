package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    REJECTED("Rejected");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}
