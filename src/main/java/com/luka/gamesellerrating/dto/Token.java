package com.luka.gamesellerrating.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luka.gamesellerrating.enums.TokenType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token {

    private String token;
    private String email;
    private LocalDate expiryDate;
    private TokenType tokenType;

    private Token(String email, TokenType type) {
        this.email = email;
        this.tokenType = type;
        this.token = UUID.randomUUID().toString();
        this.expiryDate = LocalDate.now().plusDays(type.getDuration().toDays());
    }

    public static Token create(String email, TokenType type){
        return new Token(email, type);
    }

    public boolean isValid() {
        return expiryDate.isAfter(LocalDate.now());
    }
}
