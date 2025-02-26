package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum UserStatus {

    PENDING("Pending"), VERIFIED("Verified"), REJECTED("Rejected");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }
}
