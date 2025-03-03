package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum Role {

    Admin("Admin"), Seller("Seller"), AnonymousUser("AnonymousUser");

    private final String value;

    Role(String value) {
        this.value = value;
    }

}
