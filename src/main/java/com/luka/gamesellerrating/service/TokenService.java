package com.luka.gamesellerrating.service;

import com.luka.gamesellerrating.dto.TokenDTO;
import com.luka.gamesellerrating.enums.TokenType;

public interface TokenService {

    TokenDTO generateToken(String email, TokenType type);
    void validateToken(String email, String tokenVal, TokenType type);
}

