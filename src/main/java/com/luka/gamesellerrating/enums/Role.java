package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum Role {

    ADMIN("Admin"), SELLER("Seller"), ANONYMOUS_USER("AnonymousUser");

    private final String value;

    Role(String value) {
        this.value = value;
    }

}
