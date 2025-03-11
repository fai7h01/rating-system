package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.Token;
import com.luka.gamesellerrating.enums.TokenType;
import com.luka.gamesellerrating.exception.InvalidTokenException;
import com.luka.gamesellerrating.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    private final RedisTemplate<String, Token> redisTemplate;

    public TokenServiceImpl(RedisTemplate<String, Token> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public Token generateToken(String email, TokenType tokenType) {
        validateEmail(email);
        Token token = Token.create(email, tokenType);
        storeTokenInCache(token, tokenType);
        log.debug("Generated {} token for email: {}", tokenType, email);
        return token;
    }

    @Override
    public void validateToken(String email, String tokenVal, TokenType tokenType) {
        validateTokenRequest(email, tokenVal);
        Token storedToken = getStoredTokenFromCache(email, tokenType);
        validateStoredToken(storedToken, tokenVal);
        removeTokenFromCache(email, tokenType);
        log.debug("Token validated and removed for email: {}", email);
    }

    private void storeTokenInCache(Token token, TokenType type) {
        String key = buildKey(token.getEmail(), type);
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, token, type.getDuration());
    }

    private void validateTokenRequest(String email, String tokenValue) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(tokenValue)) {
            throw new InvalidTokenException("Email and(or) token are required");
        }
    }

    private void validateStoredToken(Token token, String tokenValue) {
        if (token == null) {
            throw new InvalidTokenException("Token not found");
        }
        if (!token.isValid()) {
            throw new InvalidTokenException("Token has expired");
        }
        if (!token.getToken().equals(tokenValue)) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    private Token getStoredTokenFromCache(String email, TokenType type) {
        return redisTemplate.opsForValue().get(buildKey(email, type));
    }

    private void removeTokenFromCache(String email, TokenType type) {
        redisTemplate.delete(buildKey(email, type));
    }

    private String buildKey(String email, TokenType type) {
        return type.getRedisPrefix() + email;
    }
}
