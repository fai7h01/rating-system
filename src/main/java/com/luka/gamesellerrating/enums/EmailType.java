package com.luka.gamesellerrating.enums;

import lombok.Getter;

@Getter
public enum EmailType {

    VERIFICATION_EMAIL("Email Verification"),
    RESET_PASSWORD_EMAIL("Reset Password");

    private final String value;

    EmailType(String value) {
        this.value = value;
    }
}
