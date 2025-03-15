package com.luka.gamesellerrating.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum TokenType {

    VERIFICATION_TOKEN("verification"),
    RESET_PASSWORD_TOKEN("reset_password");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public Duration getDuration() {
        return switch (this) {
            case VERIFICATION_TOKEN -> Duration.ofDays(1);
            case RESET_PASSWORD_TOKEN -> Duration.ofHours(1);
        };
    }

    public String getRedisPrefix() {
        return this.value + ":";
    }
}