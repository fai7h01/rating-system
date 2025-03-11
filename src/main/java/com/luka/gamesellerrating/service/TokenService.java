package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.Token;
import com.luka.gamesellerrating.enums.TokenType;

public interface TokenService {

    Token generateToken(String email, TokenType type);
    void validateToken(String email, String tokenVal, TokenType type);
}

