package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum Role {

    ADMIN("Admin"), SELLER("Seller"), BUYER("BUYER");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}
