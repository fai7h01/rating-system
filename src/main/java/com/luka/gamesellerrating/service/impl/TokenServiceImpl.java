package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.TokenDTO;
import com.luka.gamesellerrating.enums.TokenType;
import com.luka.gamesellerrating.service.TokenService;
import com.luka.gamesellerrating.service.helper.TokenCacheManager;
import com.luka.gamesellerrating.service.helper.TokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenCacheManager tokenCacheManager;
    private final TokenValidator tokenValidator;

    @Override
    @Transactional
    public TokenDTO generateToken(String email, TokenType tokenType) {
        tokenValidator.validateEmail(email);
        var token = TokenDTO.create(email, tokenType);
        tokenCacheManager.storeToken(token, tokenType);
        return token;
    }

    @Override
    public void validateToken(String email, String tokenVal, TokenType tokenType) {
        tokenValidator.validateTokenRequest(email, tokenVal);
        var storedToken = tokenCacheManager.getToken(email, tokenType);
        tokenValidator.validateStoredToken(storedToken, tokenVal);
        tokenCacheManager.removeToken(email, tokenType);
    }
}
