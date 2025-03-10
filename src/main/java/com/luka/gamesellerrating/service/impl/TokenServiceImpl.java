package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.Token;
import com.luka.gamesellerrating.enums.TokenType;
import com.luka.gamesellerrating.service.TokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TokenServiceImpl implements TokenService {

    private final RedisTemplate<String, Token> redisTemplate;

    public TokenServiceImpl(RedisTemplate<String, Token> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public Token generateToken(String email, TokenType tokenType) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }
        Token token = Token.create(email, tokenType);
        String key = tokenType.getRedisPrefix().concat(email);
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, token, tokenType.getDuration());
        return token;
    }

    @Override
    @Transactional
    public boolean isTokenValid(String email, String tokenVal, TokenType type) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(tokenVal)) {
            throw new IllegalArgumentException("Email and token are required");
        }
        String key = type.getRedisPrefix() + email;
        Token token = redisTemplate.opsForValue().get(key);

        if (token != null && token.getToken().equals(tokenVal) && token.isValid()) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
