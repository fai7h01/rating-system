package com.luka.gamesellerrating.service.impl;

import com.luka.gamesellerrating.dto.TokenDTO;
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

    private final RedisTemplate<String, TokenDTO> redisTemplate;

    public TokenServiceImpl(RedisTemplate<String, TokenDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public TokenDTO generateToken(String email, TokenType tokenType) {
        validateEmail(email);
        TokenDTO tokenDTO = TokenDTO.create(email, tokenType);
        storeTokenInCache(tokenDTO, tokenType);
        log.debug("Generated {} tokenDTO for email: {}", tokenType, email);
        return tokenDTO;
    }

    @Override
    public void validateToken(String email, String tokenVal, TokenType tokenType) {
        validateTokenRequest(email, tokenVal);
        TokenDTO storedTokenDTO = getStoredTokenFromCache(email, tokenType);
        validateStoredToken(storedTokenDTO, tokenVal);
        removeTokenFromCache(email, tokenType);
        log.debug("TokenDTO validated and removed for email: {}", email);
    }

    private void storeTokenInCache(TokenDTO tokenDTO, TokenType type) {
        String key = buildKey(tokenDTO.getEmail(), type);
        redisTemplate.delete(key);
        redisTemplate.opsForValue().set(key, tokenDTO, type.getDuration());
    }

    private void validateTokenRequest(String email, String tokenValue) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(tokenValue)) {
            throw new InvalidTokenException("Email and(or) token are required");
        }
    }

    private void validateStoredToken(TokenDTO tokenDTO, String tokenValue) {
        if (tokenDTO == null) {
            throw new InvalidTokenException("TokenDTO not found");
        }
        if (!tokenDTO.isValid()) {
            throw new InvalidTokenException("TokenDTO has expired");
        }
        if (!tokenDTO.getToken().equals(tokenValue)) {
            throw new InvalidTokenException("Invalid tokenDTO");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    private TokenDTO getStoredTokenFromCache(String email, TokenType type) {
        return redisTemplate.opsForValue().get(buildKey(email, type));
    }

    private void removeTokenFromCache(String email, TokenType type) {
        redisTemplate.delete(buildKey(email, type));
    }

    private String buildKey(String email, TokenType type) {
        return type.getRedisPrefix() + email;
    }
}
